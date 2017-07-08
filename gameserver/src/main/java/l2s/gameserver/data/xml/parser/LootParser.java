package l2s.gameserver.data.xml.parser;

import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.LootHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.model.reward.RewardGroup;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.Loot;
import l2s.gameserver.templates.npc.NpcTemplate;
import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;

public class LootParser extends AbstractDirParser<LootHolder> {
    private static final LootParser instance = new LootParser();

    private LootParser() {
        super(LootHolder.getInstance());
    }

    public static LootParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLDir() {
        return new File(Config.DATAPACK_ROOT, "data/loot/");
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    protected void readData(Element rootElement) throws Exception {
        for (Iterator<Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext();) {
            org.dom4j.Element npcElement = npcIterator.next();
            int npcId = Integer.parseInt(npcElement.attributeValue("id"));
            NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
            Loot loot = new Loot(npcId);
            if (template == null) {
                warn("Can't load loot for npc (npcId: " + npcId + ") because NpcTemplate is null");
                return;
            }

            for (Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator(); secondIterator.hasNext(); ) {
                org.dom4j.Element secondElement = secondIterator.next();
                String nodeName = secondElement.getName();
                if (nodeName.equalsIgnoreCase("rewardlist")) {
                    RewardType type = RewardType.valueOf(secondElement.attributeValue("type"));
                    boolean autoLoot = secondElement.attributeValue("auto_loot") != null && Boolean.parseBoolean(secondElement.attributeValue("auto_loot"));
                    RewardList rewards = new RewardList(type, autoLoot);

                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        final org.dom4j.Element nextElement = nextIterator.next();
                        final String nextName = nextElement.getName();
                        if (nextName.equalsIgnoreCase("group")) {
                            double enterChance = nextElement.attributeValue("chance") == null ? RewardList.MAX_CHANCE : Double.parseDouble(nextElement.attributeValue("chance")) * 10000;

                            RewardGroup group = (type == RewardType.SWEEP || type == RewardType.NOT_RATED_NOT_GROUPED) ? null : new RewardGroup(enterChance);
                            for (Iterator<org.dom4j.Element> rewardIterator = nextElement.elementIterator(); rewardIterator.hasNext(); ) {
                                org.dom4j.Element rewardElement = rewardIterator.next();
                                RewardData data = parseReward(rewardElement);
                                if (type == RewardType.SWEEP || type == RewardType.NOT_RATED_NOT_GROUPED) {
                                    warn("Can't load rewardlist from group: " + npcId + "; type: " + type);
                                }
                                else {
                                    group.addData(data);
                                }
                            }

                            if (group != null) {
                                rewards.add(group);
                            }
                        }
                        else if (nextName.equalsIgnoreCase("reward")) {
                            if (type != RewardType.SWEEP && type != RewardType.NOT_RATED_NOT_GROUPED) {
                                warn("Reward can't be without group(and not grouped): " + npcId + "; type: " + type);
                                continue;
                            }

                            RewardData data = parseReward(nextElement);
                            RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
                            g.addData(data);
                            rewards.add(g);
                        }
                    }

                    loot.addRewards(rewards, type);
                }
            }

            for (Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator("database_rewardlist"); secondIterator.hasNext(); ) {
                org.dom4j.Element secondElement = secondIterator.next();

                RewardList list = new RewardList(RewardType.RATED_GROUPED, false);
                if (!template.isInstanceOf(RaidBossInstance.class)) {
                    RewardGroup equipAndPiecesGroup = null;
                    RewardGroup etcGroup = null;

                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator("reward"); nextIterator.hasNext(); ) {
                        final org.dom4j.Element nextElement = nextIterator.next();

                        RewardData data = parseReward(nextElement);
                        ItemTemplate itemTemplate = data.getItem();
                        if (itemTemplate.isAdena()) {
                            RewardGroup adenaGroup = new RewardGroup(data.getChance());
                            data.setChance(RewardList.MAX_CHANCE);
                            adenaGroup.addData(data);
                            list.add(adenaGroup);
                        }
                        else if (itemTemplate.isArmor() || itemTemplate.isWeapon() || itemTemplate.isAccessory() || itemTemplate.isKeyMatherial()) {
                            if (equipAndPiecesGroup == null) {
                                equipAndPiecesGroup = new RewardGroup(RewardList.MAX_CHANCE);
                            }
                            equipAndPiecesGroup.addData(data);
                        }
                        else {
                            if (etcGroup == null) {
                                etcGroup = new RewardGroup(RewardList.MAX_CHANCE);
                            }
                            etcGroup.addData(data);
                        }
                    }

                    if (equipAndPiecesGroup != null) {
                        equipAndPiecesGroup.setChance(200000);

                        for (RewardData data : equipAndPiecesGroup.getItems()) {
                            data.setChance(data.getChance() * 5);
                        }

                        list.add(equipAndPiecesGroup);
                    }

                    if (etcGroup != null) {
                        etcGroup.setChance(500000);

                        for (RewardData data : etcGroup.getItems()) {
                            data.setChance(data.getChance() * 2);
                        }

                        list.add(etcGroup);
                    }
                }
                else {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator("reward"); nextIterator.hasNext(); ) {
                        final org.dom4j.Element nextElement = nextIterator.next();

                        RewardGroup group = new RewardGroup(RewardList.MAX_CHANCE);
                        group.addData(parseReward(nextElement));
                        list.add(group);
                    }
                }

                loot.addRewards(list);
            }

            LootHolder.getInstance().addLoot(loot);
            template.setRewards(loot.getRewards());
        }
    }

    private RewardData parseReward(org.dom4j.Element rewardElement) {
        int itemId = Integer.parseInt(rewardElement.attributeValue("item_id"));
        int min = Integer.parseInt(rewardElement.attributeValue("min"));
        int max = Integer.parseInt(rewardElement.attributeValue("max"));
        // переводим в системный вид
        int chance = (int) (Double.parseDouble(rewardElement.attributeValue("chance")) * 10000);

        RewardData data = new RewardData(itemId);
        data.setChance(chance);

        data.setMinDrop(min);
        data.setMaxDrop(max);

        return data;
    }
}
