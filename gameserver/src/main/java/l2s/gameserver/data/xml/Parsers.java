package l2s.gameserver.data.xml;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.data.string.SkillNameHolder;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.data.xml.parser.*;
import l2s.gameserver.instancemanager.ReflectionManager;

/**
 * @author VISTALL
 * @date 20:55/30.11.2010
 */
public abstract class Parsers {
    public static void parseAll() {
        HtmCache.getInstance().reload();
        StringsHolder.getInstance().load();
        ItemNameHolder.getInstance().load();
        SkillNameHolder.getInstance().load();
        //
        SkillEnchantInfoParser.getInstance().load();
        SkillParser.getInstance().load();
        OptionDataParser.getInstance().load();
        VariationDataParser.getInstance().load();
        ItemParser.getInstance().load();
        RecipeParser.getInstance().load();
        AlchemyDataParser.getInstance().load();
        CrystallizationDataParser.getInstance().load();
        SynthesisDataParser.getInstance().load();
        //
        BaseStatsBonusParser.getInstance().load();
        BeautyShopParser.getInstance().load();
        LevelBonusParser.getInstance().load();
        KarmaIncreaseDataParser.getInstance().load();
        HitCondBonusParser.getInstance().load();
        PlayerTemplateParser.getInstance().load();
        ClassDataParser.getInstance().load();
        TransformTemplateParser.getInstance().load();
        NpcParser.getInstance().load();
        LootParser.getInstance().load();
        PetDataParser.getInstance().load();

        DomainParser.getInstance().load();
        RestartPointParser.getInstance().load();

        StaticObjectParser.getInstance().load();
        DoorParser.getInstance().load();
        ZoneParser.getInstance().load();
        SpawnParser.getInstance().load();
        StatuesSpawnParser.getInstance().load();
        InstantZoneParser.getInstance().load();

        ReflectionManager.getInstance().init();
        //
        AirshipDockParser.getInstance().load();
        SkillAcquireParser.getInstance().load();
        SkillAcquireParser.getInstance().afterParseActions();
        //
        ResidenceParser.getInstance().load();
        ShuttleTemplateParser.getInstance().load();
        EventParser.getInstance().load();
        if (Config.ALLOW_FIGHT_CLUB) {
            FightClubMapParser.getInstance().load();
        }
        // support(cubic & agathion)
        CubicParser.getInstance().load();
        //
        BuyListParser.getInstance().load();
        MultiSellParser.getInstance().load();
        ProductDataParser.getInstance().load();
        // AgathionParser.getInstance();
        // item support
        HennaParser.getInstance().load();
        JumpTracksParser.getInstance().load();
        EnchantItemParser.getInstance().load();
        EnchantStoneParser.getInstance().load();
        AttributeStoneParser.getInstance().load();
        AppearanceStoneParser.getInstance().load();
        SoulCrystalParser.getInstance().load();
        ArmorSetsParser.getInstance().load();
        FishDataParser.getInstance().load();

        LevelUpRewardParser.getInstance().load();

        FakePlayerPathParser.getInstance().load();
        FakePlayersParser.getInstance().load();

        // etc
        PetitionGroupParser.getInstance().load();
    }
}
