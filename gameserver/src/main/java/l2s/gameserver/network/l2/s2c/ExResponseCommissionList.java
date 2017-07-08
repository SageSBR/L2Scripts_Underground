package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.CommissionItem;

/**
 * @author Bonux
 */
public class ExResponseCommissionList extends L2GameServerPacket
{
	public final static int COMMON_EMPTY_LIST = -1;
	public final static int MY_EMPTY_LIST = -2;
	public final static int MY_COMMISSION_LIST = 2;
	public final static int COMMON_COMMISSION_LIST = 3;

	public final static L2GameServerPacket COMMON_EMPTY_LIST_PACKET = new ExResponseCommissionList(COMMON_EMPTY_LIST);
	public final static L2GameServerPacket MY_EMPTY_LIST_PACKET = new ExResponseCommissionList(MY_EMPTY_LIST);

	private final int _currentTimeInSeconds;
	private final int _listType;
	private final int _part;
	private final CommissionItem[] _items;

	private ExResponseCommissionList(int listType)
	{
		_currentTimeInSeconds = 0;
		_listType = listType;
		_part = 0;
		_items = new CommissionItem[0];
	}

	public ExResponseCommissionList(int listType, int part, CommissionItem[] items)
	{
		_currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000L);
		_listType = listType;
		_part = part;
		_items = items;
	}

	protected void writeImpl()
	{
		writeD(_listType);
		if(_listType == COMMON_EMPTY_LIST || _listType == MY_EMPTY_LIST)
			return;

		writeD(_currentTimeInSeconds); //Current time in seconds
		writeD(_part); //??
		writeD(_items.length); //items count
		for(CommissionItem item : _items)
		{
			writeQ(item.getCommissionId()); //bid id
			writeQ(item.getCommissionPrice()); //price
			writeD(item.getItem().getExType().ordinal());
			writeD(item.getPeriodDays());
			writeD(item.getEndPeriodDate()); //end date in seconds
			writeS(item.getOwnerName()); //Owner Name
			writeD(0x00); //unk
			writeD(item.getItemId()); //item_id
			writeQ(item.getCount()); //count
			writeH(item.getItem().getType2()); //itemType2 or equipSlot
			writeQ(item.getItem().getBodyPart()); //bodypart
			writeH(item.getEnchantLevel()); //enchant_lvl
			writeH(item.getCustomType2());
			writeH(item.getAttackElement()); //atk_element_id
			writeH(item.getAttackElementValue()); //atk_element_val
			writeH(item.getDefenceFire()); //fire_defence
			writeH(item.getDefenceWater()); //water_defence
			writeH(item.getDefenceWind()); //wind_defence
			writeH(item.getDefenceEarth()); //earth_defence
			writeH(item.getDefenceHoly()); //holy_defence
			writeH(item.getDefenceUnholy()); //unholy_defence
			writeD(item.getEnchantOptions()[0]); //enchant_opt1
			writeD(item.getEnchantOptions()[1]); //enchant_opt2
			writeD(item.getEnchantOptions()[2]); //enchant_opt3
			writeD(item.getVisualId());
		}
	}
}
