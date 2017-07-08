package l2s.gameserver.model.base;

/**
 * Author: VISTALL
 * Date:  11:53/01.12.2010
 */
public enum AcquireType
{
	/*0*/NORMAL(0),
	/*1*/FISHING(1),
	/*2*/CLAN(2),
	/*3*/SUB_UNIT(3),
	/*4*/TRANSFORMATION(4),
	/*5*/CERTIFICATION(5),
	/*6*/DUAL_CERTIFICATION(6),
	/*7*/COLLECTION(7),
	/*8*/TRANSFER_CARDINAL(8),
	/*9*/TRANSFER_EVA_SAINTS(9),
	/*10*/TRANSFER_SHILLIEN_SAINTS(10),
	/*11*/GENERAL(11),
	/*12*/NOBLESSE(12),
	/*13*/HERO(13),
	/*14*/GM(14),
	/*15*/CHAOS(15),
	/*16*/DUAL_CHAOS(16),
	/*17*/ABILITY(17),
	/*18*/HONORABLE_NOBLESSE(18),
	/*19*/ALCHEMY(140);

	public static final AcquireType[] VALUES = AcquireType.values();

	private final int _id;

	private AcquireType(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public static AcquireType getById(int id)
	{
		for(AcquireType at : VALUES)
		{
			if(at.getId() == id)
				return at;
		}
		return null;
	}

	public static AcquireType transferType(int classId)
	{
		switch(classId)
		{
			case 97:
				return TRANSFER_CARDINAL;
			case 105:
				return TRANSFER_EVA_SAINTS;
			case 112:
				return TRANSFER_SHILLIEN_SAINTS;
		}

		return null;
	}

	public int transferClassId()
	{
		switch(this)
		{
			case TRANSFER_CARDINAL:
				return 97;
			case TRANSFER_EVA_SAINTS:
				return 105;
			case TRANSFER_SHILLIEN_SAINTS:
				return 112;
		}

		return 0;
	}
}
