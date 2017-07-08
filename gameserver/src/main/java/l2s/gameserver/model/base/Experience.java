package l2s.gameserver.model.base;

import l2s.gameserver.Config;

public class Experience
{
	public final static long LEVEL[] = { -1L, // level 0 (unreachable)
			/* Lvl:1 */0L,
			/* Lvl:2 */68L,
			/* Lvl:3 */363L,
			/* Lvl:4 */1168L,
			/* Lvl:5 */2884L,
			/* Lvl:6 */6038L,
			/* Lvl:7 */11287L,
			/* Lvl:8 */19423L,
			/* Lvl:9 */31378L,
			/* Lvl:10 */48229L,
			/* Lvl:11 */71202L,
			/* Lvl:12 */101677L,
			/* Lvl:13 */141193L,
			/* Lvl:14 */191454L,
			/* Lvl:15 */254330L,
			/* Lvl:16 */331867L,
			/* Lvl:17 */426288L,
			/* Lvl:18 */540000L,
			/* Lvl:19 */675596L,
			/* Lvl:20 */835862L,
			/* Lvl:21 */920357L,
			/* Lvl:22 */1015431L,
			/* Lvl:23 */1123336L,
			/* Lvl:24 */1246808L,
			/* Lvl:25 */1389235L,
			/* Lvl:26 */1554904L,
			/* Lvl:27 */1749413L,
			/* Lvl:28 */1980499L,
			/* Lvl:29 */2260321L,
			/* Lvl:30 */2634751L,
			/* Lvl:31 */2844287L,
			/* Lvl:32 */3093068L,
			/* Lvl:33 */3389496L,
			/* Lvl:34 */3744042L,
			/* Lvl:35 */4169902L,
			/* Lvl:36 */4683988L,
			/* Lvl:37 */5308556L,
			/* Lvl:38 */6074376L,
			/* Lvl:39 */7029248L,
			/* Lvl:40 */8342182L,
			/* Lvl:41 */8718976L,
			/* Lvl:42 */9289560L,
			/* Lvl:43 */9991807L,
			/* Lvl:44 */10856075L,
			/* Lvl:45 */11920512L,
			/* Lvl:46 */13233701L,
			/* Lvl:47 */14858961L,
			/* Lvl:48 */16882633L,
			/* Lvl:49 */19436426L,
			/* Lvl:50 */22977080L,
			/* Lvl:51 */24605660L,
			/* Lvl:52 */26635948L,
			/* Lvl:53 */29161263L,
			/* Lvl:54 */32298229L,
			/* Lvl:55 */36193556L,
			/* Lvl:56 */41033917L,
			/* Lvl:57 */47093035L,
			/* Lvl:58 */54711546L,
			/* Lvl:59 */64407353L,
			/* Lvl:60 */77947292L,
			/* Lvl:61 */85775204L,
			/* Lvl:62 */95595386L,
			/* Lvl:63 */107869713L,
			/* Lvl:64 */123174171L,
			/* Lvl:65 */142229446L,
			/* Lvl:66 */165944812L,
			/* Lvl:67 */195677269L,
			/* Lvl:68 */233072222L,
			/* Lvl:69 */280603594L,
			/* Lvl:70 */335732975L,
			/* Lvl:71 */383597045L,
			/* Lvl:72 */442752112L,
			/* Lvl:73 */516018015L,
			/* Lvl:74 */606913902L,
			/* Lvl:75 */719832095L,
			/* Lvl:76 */860289228L,
			/* Lvl:77 */1035327669L,
			/* Lvl:78 */1259458516L,
			/* Lvl:79 */1534688053L,
			/* Lvl:80 */1909610088L,
			/* Lvl:81 */2342785974L,
			/* Lvl:82 */2861857696L,
			/* Lvl:83 */3478378664L,
			/* Lvl:84 */4211039578L,
			/* Lvl:85 */5078544041L,
			/* Lvl:86 */10985069426L,
			/* Lvl:87 */19192594397L,
			/* Lvl:88 */33533938399L,
			/* Lvl:89 */43503026615L,
			/* Lvl:90 */63751938490L,
			/* Lvl:91 */88688523458L,
			/* Lvl:92 */120224273113L,
			/* Lvl:93 */157133602347L,
			/* Lvl:94 */208513860393L,
			/* Lvl:95 */  266769078393L,
			/* Lvl:96 */  377839508352L,
			/* Lvl:97 */  592791113370L,
			/* Lvl:98 */ 1016243369039L,
			/* Lvl:99 */ 1956916677389L, /* diff=4221464047612 */
			/* Lvl:100 */6178380725001L, /* diff=2294681576453 !!  new_diff=8442928095224 */
			/* Lvl:101 */14621308820225L, /* old=8473062301454 */
			/* Lvl:102 */43523955372407L,
			/* Lvl:103 */84422086452639L,
			/* Lvl:104 */188752303396110L,
			/* Lvl:105 */358035537321346L,
			/* Lvl:106 */3514822095225670L,
			/* Lvl:107 */7302965964710870L,
			//TODO: Update EXP value after 107 lvl.
			/* Lvl:108 */14602965964710870L,
			/* Lvl:109 */29202965964710870L,
			/* Lvl:110 */58402965964710870L,
			/* Lvl:111 */70002965964710870L,
			/* Lvl:112 */84002965964710870L,
			/* Lvl:113 */100902965964710870L,
			/* Lvl:114 */121002965964710870L,
			/* Lvl:115 */145302965964710870L,
			/* Lvl:116 */174302965964710870L,
			/* Lvl:117 */209102965964710870L,
			/* Lvl:118 */250902965964710870L,
			/* Lvl:119 */301102965964710870L,
			/* Lvl:120 */361402965964710870L,
			/* Lvl:121 */433702965964710870L,
			/* Lvl:122 */520402965964710870L,
			/* Lvl:123 */624502965964710870L,
			/* Lvl:124 */749402965964710870L,
			/* Lvl:125 */1498802965964710870L,
			/* Lvl:126 */2997602965964710870L,
			/* Lvl:127 */5995202965964710870L,
			/* Lvl:128 */Long.MAX_VALUE };

	/**
	 * Return PenaltyModifier (can use in all cases)
	 *
	 * @param count	- how many times <percents> will be substructed
	 * @param percents - percents to substruct
	 *
	 * @author Styx
	 */

	/*
	 *  This is for fine view only ;)
	 *
	 *	public final static double penaltyModifier(int count, int percents)
	 *	{
	 *		int allPercents = 100;
	 *		int allSubstructedPercents = count * percents;
	 *		int penaltyInPercents = allPercents - allSubstructedPercents;
	 *		double penalty = penaltyInPercents / 100.0;
	 *		return penalty;
	 *	}
	 */
	public static double penaltyModifier(long count, double percents)
	{
		return Math.max(1. - count * percents / 100, 0);
	}

	/**
	 * Максимальный достижимый уровень
	 */
	public static int getMaxLevel()
	{
		return Config.ALT_MAX_LEVEL;
	}

	/**
	 * Максимальный уровень для саба
	 */
	public static int getMaxSubLevel()
	{
		return Config.ALT_MAX_SUB_LEVEL;
	}

	public static int getLevel(long thisExp)
	{
		int level = 0;
		for(int i = 0; i < LEVEL.length; i++)
		{
			long exp = LEVEL[i];
			if(thisExp >= exp)
				level = i;
		}
		return level;
	}

	public static long getExpForLevel(int lvl)
	{
		if(lvl >= Experience.LEVEL.length)
			return 0;
		return Experience.LEVEL[lvl];
	}

	public static double getExpPercent(int level, long exp)
	{
		return (exp - getExpForLevel(level)) / ((getExpForLevel(level + 1) - getExpForLevel(level)) / 100.0D) * 0.01D;
	}
}