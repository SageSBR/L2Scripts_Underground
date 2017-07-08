package l2s.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NpcHolder extends AbstractHolder {
    private static final NpcHolder instance = new NpcHolder();

    private TIntObjectHashMap<NpcTemplate> npcs = new TIntObjectHashMap<>(20000);
    private TIntObjectHashMap<List<NpcTemplate>> npcsByLevel;
    private NpcTemplate[] allTemplates;
    private Map<String, NpcTemplate> npcsNames;

    public static NpcHolder getInstance() {
        return instance;
    }

    NpcHolder() {

    }

    public void addTemplate(NpcTemplate template) {
        npcs.put(template.getId(), template);
    }

    public NpcTemplate getTemplate(int id) {
        NpcTemplate npc = ArrayUtils.valid(allTemplates, id);
        if (npc == null) {
            warn("Not defined npc id : " + id + ", or out of range!", new Exception());
            return null;
        }
        return allTemplates[id];
    }

    public NpcTemplate getTemplateByName(String name) {
        return npcsNames.get(name.toLowerCase());
    }

    public List<NpcTemplate> getAllOfLevel(int lvl) {
        return npcsByLevel.get(lvl);
    }

    public NpcTemplate[] getAll() {
        return npcs.values(new NpcTemplate[npcs.size()]);
    }

    private void buildFastLookupTable() {
        npcsByLevel = new TIntObjectHashMap<>();
        npcsNames = new HashMap<>();

        int highestId = 0;
        for (int id : npcs.keys()) {
            if (id > highestId) {
                highestId = id;
            }
        }

        allTemplates = new NpcTemplate[highestId + 1];
        for (TIntObjectIterator<NpcTemplate> iterator = npcs.iterator(); iterator.hasNext(); ) {
            iterator.advance();
            int npcId = iterator.key();
            NpcTemplate npc = iterator.value();

            allTemplates[npcId] = npc;

            List<NpcTemplate> byLevel;
            if ((byLevel = npcsByLevel.get(npc.level)) == null) {
                npcsByLevel.put(npcId, byLevel = new ArrayList<NpcTemplate>());
            }
            byLevel.add(npc);

            npcsNames.put(npc.name.toLowerCase(), npc);
        }
    }

    @Override
    protected void process() {
        buildFastLookupTable();
    }

    @Override
    public int size() {
        return npcs.size();
    }

    @Override
    public void clear() {
        npcsNames.clear();
        npcs.clear();
    }
}
