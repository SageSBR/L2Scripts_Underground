package l2s.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.database.mysql;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExBR_GamePointPacket;
import l2s.gameserver.network.l2.s2c.ExBR_PresentBuyProductPacket;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.network.l2.s2c.ExReplyWritePost;
import l2s.gameserver.network.l2.s2c.ExUnReadMailCount;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author FW-Team && Bonux
 */
public class RequestBR_PresentBuyProduct extends L2GameClientPacket {
    private int productId;
    private int count;
    private String receiverName;
    private String topic;
    private String message;

    protected void readImpl() throws Exception {
        this.productId = readD();
        this.count = readD();
        this.receiverName = readS();
        this.topic = readS();
        this.message = readS();
    }

    protected void runImpl() throws Exception {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (count > 99 || count < 0) {
            return;
        }

        ProductItem product = ProductDataHolder.getInstance().getProduct(productId);
        if (product == null) {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_WRONG_PRODUCT);
            return;
        }

        if (!product.isOnSale() || (System.currentTimeMillis() < product.getStartTimeSale()) || (System.currentTimeMillis() > product.getEndTimeSale())) {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_SALE_PERIOD_ENDED);
            return;
        }

        final int pointsRequired = product.getPrice(true) * count;
        if (pointsRequired < 0) {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_WRONG_PRODUCT);
            return;
        }

        final long pointsCount = activeChar.getPremiumPoints();
        if (pointsRequired > pointsCount) {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
            return;
        }

        Player receiver = World.getPlayer(receiverName);
        int receiverId;
        if (receiver != null) {
            receiverId = receiver.getObjectId();
            receiverName = receiver.getName();
            if (receiver.getBlockList().contains(activeChar)) {
                activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(receiverName));
                return;
            }
        }
        else {
            receiverId = CharacterDAO.getInstance().getObjectIdByName(receiverName);
            if (receiverId > 0) {
                if (mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + receiverId + " AND target_Id=" + activeChar.getObjectId()) > 0) {
                    activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(receiverName));
                    return;
                }
            }
        }

        if (receiverId == 0) {
            activeChar.sendPacket(SystemMsg.WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return;
        }

        if (!activeChar.reducePremiumPoints(pointsRequired)) {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
            return;
        }

        activeChar.getProductHistoryList().onPurchaseProduct(product);

        List<ItemInstance> attachments = new ArrayList<>();
        for (ProductItemComponent comp : product.getComponents()) {
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(comp.getItemId());
            if (itemTemplate == null) {
                continue;
            }

            if (itemTemplate.isStackable()) {
                ItemInstance item = ItemFunctions.createItem(itemTemplate.getItemId());
                item.setCount(comp.getCount() * count);
                item.setOwnerId(activeChar.getObjectId());
                item.setLocation(ItemInstance.ItemLocation.MAIL);
                if (item.getJdbcState().isSavable()) {
                    item.save();
                }
                else {
                    item.setJdbcState(JdbcEntityState.UPDATED);
                    item.update();
                }
                attachments.add(item);
            }
            else {
                ItemInstance item;
                long count = comp.getCount() * this.count;
                for (long i = 0; i < count; i++) {
                    item = ItemFunctions.createItem(itemTemplate.getItemId());
                    item.setCount(1);
                    item.setOwnerId(activeChar.getObjectId());
                    item.setLocation(ItemInstance.ItemLocation.MAIL);
                    if (item.getJdbcState().isSavable()) {
                        item.save();
                    }
                    else {
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();
                    }
                    attachments.add(item);
                }
            }
        }

        Mail mail = new Mail();
        mail.setSenderId(activeChar.getObjectId());
        mail.setSenderName(activeChar.getName());
        mail.setReceiverId(receiverId);
        mail.setReceiverName(receiverName);
        mail.setTopic(topic);
        mail.setBody(message);
        mail.setPrice(0L);
        mail.setUnread(true);
        mail.setType(Mail.SenderType.PRESENT);
        mail.setExpireTime(1296000 + (int) (System.currentTimeMillis() / 1000L)); //15 суток дается.
        for (ItemInstance item : attachments) {
            mail.addAttachment(item);
        }
        mail.save();

        activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
        activeChar.sendPacket(new ExBR_GamePointPacket(activeChar));
        activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_OK);
        activeChar.sendChanges();

        if (receiver != null) {
            receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
            receiver.sendPacket(new ExUnReadMailCount(receiver));
            receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
        }
    }
}