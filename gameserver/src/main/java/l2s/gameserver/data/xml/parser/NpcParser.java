package l2s.gameserver.data.xml.parser;

import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.npc.*;
import l2s.gameserver.utils.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NpcParser extends AbstractDirParser<NpcHolder> {
    private static final NpcParser instance = new NpcParser();

    private NpcParser() {
        super(NpcHolder.getInstance());
    }

    public static NpcParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLDir() {
        return new File(Config.DATAPACK_ROOT, "data/npc/");
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    protected void readData(org.dom4j.Element rootElement) throws Exception {
        for (Iterator<org.dom4j.Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext();) {
            org.dom4j.Element npcElement = npcIterator.next();
            int npcId = Integer.parseInt(npcElement.attributeValue("id"));
            int templateId = npcElement.attributeValue("template_id") == null ? 0 : Integer.parseInt(npcElement.attributeValue("template_id"));
            String name = npcElement.attributeValue("name");
            String title = npcElement.attributeValue("title");
            String forceName = npcElement.attributeValue("forceName");
            String forceTitle = npcElement.attributeValue("forceTitle");

            StatsSet set = new StatsSet();
            set.set("npcId", npcId);
            set.set("displayId", templateId);
            set.set("name", name);
            set.set("title", title);
            set.set("baseCpReg", 0);
            set.set("baseCpMax", 0);
            set.set("forceName", forceName == null ? "" : forceName);
            set.set("forceTitle", forceTitle == null ? "" : forceTitle);

            for (Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext(); ) {
                org.dom4j.Element firstElement = firstIterator.next();
                if (firstElement.getName().equalsIgnoreCase("set")) {
                    set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
                }
                else if (firstElement.getName().equalsIgnoreCase("equip")) {
                    for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext(); ) {
                        org.dom4j.Element eElement = eIterator.next();
                        set.set(eElement.getName(), eElement.attributeValue("item_id"));
                    }
                }
                else if (firstElement.getName().equalsIgnoreCase("ai_params")) {
                    StatsSet ai = new StatsSet();
                    for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext(); ) {
                        org.dom4j.Element eElement = eIterator.next();
                        ai.set(eElement.attributeValue("name"), eElement.attributeValue("value"));
                    }
                    set.set("aiParams", ai);
                }
                else if (firstElement.getName().equalsIgnoreCase("attributes")) {
                    int[] attributeAttack = new int[6];
                    int[] attributeDefence = new int[6];
                    for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext(); ) {
                        org.dom4j.Element eElement = eIterator.next();
                        Element element;
                        if (eElement.getName().equalsIgnoreCase("defence")) {
                            element = Element.getElementByName(eElement.attributeValue("attribute"));
                            attributeDefence[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
                        }
                        else if (eElement.getName().equalsIgnoreCase("attack")) {
                            element = Element.getElementByName(eElement.attributeValue("attribute"));
                            attributeAttack[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
                        }
                    }

                    set.set("baseAttributeAttack", attributeAttack);
                    set.set("baseAttributeDefence", attributeDefence);
                }
            }

            NpcTemplate template = new NpcTemplate(set);

            for (Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator(); secondIterator.hasNext(); ) {
                org.dom4j.Element secondElement = secondIterator.next();
                String nodeName = secondElement.getName();
                if (nodeName.equalsIgnoreCase("faction")) {
                    String factionNames = secondElement.attributeValue("names");
                    int factionRange = Integer.parseInt(secondElement.attributeValue("range"));
                    Faction faction = new Faction(factionNames, factionRange);
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        final org.dom4j.Element nextElement = nextIterator.next();
                        int ignoreId = Integer.parseInt(nextElement.attributeValue("npc_id"));
                        faction.addIgnoreNpcId(ignoreId);
                    }
                    template.setFaction(faction);
                }
                else if (nodeName.equalsIgnoreCase("skills")) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();
                        int id = Integer.parseInt(nextElement.attributeValue("id"));
                        int level = Integer.parseInt(nextElement.attributeValue("level"));

                        // Для определения расы используется скилл 4416
                        if (id == 4416) {
                            template.setRace(level);
                        }

                        Skill skill = SkillHolder.getInstance().getSkill(id, level);

                        //TODO
                        //if(skill == null || skill.getSkillType() == L2Skill.SkillType.NOTDONE)
                        //	unimpl.add(Integer.valueOf(skillId));
                        if (skill == null) {
                            continue;
                        }

                        template.addSkill(skill);
                    }
                }
                else if (nodeName.equalsIgnoreCase("minions")) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();
                        int id = Integer.parseInt(nextElement.attributeValue("npc_id"));
                        int count = Integer.parseInt(nextElement.attributeValue("count"));

                        template.addMinion(new MinionData(id, count));
                    }
                }
                else if (nodeName.equalsIgnoreCase("absorblist")) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();

                        int chance = Integer.parseInt(nextElement.attributeValue("chance"));
                        int cursedChance = nextElement.attributeValue("cursed_chance") == null ? 0 : Integer.parseInt(nextElement.attributeValue("cursed_chance"));
                        int minLevel = Integer.parseInt(nextElement.attributeValue("min_level"));
                        int maxLevel = Integer.parseInt(nextElement.attributeValue("max_level"));
                        boolean skill = nextElement.attributeValue("skill") != null && Boolean.parseBoolean(nextElement.attributeValue("skill"));
                        AbsorbInfo.AbsorbType absorbType = AbsorbInfo.AbsorbType.valueOf(nextElement.attributeValue("type"));

                        template.addAbsorbInfo(new AbsorbInfo(skill, absorbType, chance, cursedChance, minLevel, maxLevel));
                    }
                }
                else if (nodeName.equalsIgnoreCase("teleportlist")) {
                    for (Iterator<org.dom4j.Element> sublistIterator = secondElement.elementIterator(); sublistIterator.hasNext(); ) {
                        org.dom4j.Element subListElement = sublistIterator.next();
                        int id = Integer.parseInt(subListElement.attributeValue("id"));
                        boolean prime_hours = subListElement.attributeValue("prime_hours") == null || Boolean.parseBoolean(subListElement.attributeValue("prime_hours"));
                        List<TeleportLocation> list = new ArrayList<TeleportLocation>();
                        for (Iterator<org.dom4j.Element> targetIterator = subListElement.elementIterator(); targetIterator.hasNext(); ) {
                            org.dom4j.Element targetElement = targetIterator.next();
                            int itemId = Integer.parseInt(targetElement.attributeValue("item_id", "57"));
                            long price = Integer.parseInt(targetElement.attributeValue("price"));
                            int npcStringId = Integer.parseInt(targetElement.attributeValue("name"));
                            int castleId = Integer.parseInt(targetElement.attributeValue("castle_id", "0"));
                            TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, castleId, prime_hours);
                            loc.set(Location.parseLoc(targetElement.attributeValue("loc")));
                            list.add(loc);
                        }
                        template.addTeleportList(id, list);
                    }
                }
                else if (nodeName.equalsIgnoreCase("walker_route")) {
                    int id = Integer.parseInt(secondElement.attributeValue("id"));
                    WalkerRouteType type = secondElement.attributeValue("type") == null ? WalkerRouteType.LENGTH : WalkerRouteType.valueOf(secondElement.attributeValue("type").toUpperCase());
                    WalkerRoute walkerRoute = new WalkerRoute(id, type);
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();
                        Location loc = Location.parse(nextElement);
                        NpcString phrase = nextElement.attributeValue("phrase_id") == null ? null : NpcString.valueOf(Integer.parseInt(nextElement.attributeValue("phrase_id").toUpperCase()));
                        int socialActionId = nextElement.attributeValue("social_action_id") == null ? -1 : Integer.parseInt(nextElement.attributeValue("social_action_id"));
                        int delay = nextElement.attributeValue("delay") == null ? 0 : Integer.parseInt(nextElement.attributeValue("delay"));
                        boolean running = nextElement.attributeValue("running") != null && Boolean.parseBoolean(nextElement.attributeValue("running"));
                        boolean teleport = nextElement.attributeValue("teleport") != null && Boolean.parseBoolean(nextElement.attributeValue("teleport"));
                        walkerRoute.addPoint(new WalkerRoutePoint(loc, phrase, socialActionId, delay, running, teleport));
                    }
                    template.addWalkerRoute(walkerRoute);
                }
                else if (nodeName.equalsIgnoreCase("random_actions")) {
                    boolean random_order = secondElement.attributeValue("random_order") != null && Boolean.parseBoolean(secondElement.attributeValue("random_order"));
                    RandomActions randomActions = new RandomActions(random_order);
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();
                        int id = Integer.parseInt(nextElement.attributeValue("id"));
                        NpcString phrase = nextElement.attributeValue("phrase_id") == null ? null : NpcString.valueOf(Integer.parseInt(nextElement.attributeValue("phrase_id")));
                        int socialActionId = nextElement.attributeValue("social_action_id") == null ? -1 : Integer.parseInt(nextElement.attributeValue("social_action_id"));
                        int delay = nextElement.attributeValue("delay") == null ? 0 : Integer.parseInt(nextElement.attributeValue("delay"));
                        randomActions.addAction(new RandomActions.Action(id, phrase, socialActionId, delay));
                    }
                    template.setRandomActions(randomActions);
                }
            }

            getHolder().addTemplate(template);
        }
    }
}
