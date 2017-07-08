//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import ru.akumu.smartguard.model.SoftwareType;

public enum DetectedSoftware
{
    // bots
    L2TOWER("L2Tower", SoftwareType.BOT),
    ADRENALINE("Adrenaline", SoftwareType.BOT),
    ZRANGER("ZRanger", SoftwareType.BOT),
    L2WALKER("L2Walker", SoftwareType.BOT),
    BOT_SOFT("Bot Soft", SoftwareType.BOT),

    // radars
    L2CONTROL("L2Control", SoftwareType.RADAR),
    RADAR_SOFT("Radar Soft", SoftwareType.RADAR),

    // ph soft
    L2PHX("L2Phx", SoftwareType.PACKET_HACK),
    WPF("Sauron's WPF", SoftwareType.PACKET_HACK),
    PH_SOFT("Packet Hack Soft", SoftwareType.PACKET_HACK);

    public String name;
    public SoftwareType softwareType;

    DetectedSoftware(String name, SoftwareType softwareType)
    {
        this.name = name;
        this.softwareType = softwareType;
    }

    public static List<DetectedSoftware> getList(BitSet mask)
    {
        List<DetectedSoftware> result = new ArrayList<>(3);

        for(DetectedSoftware is : values())
        {
            if(mask.get(is.ordinal()))
            {
                result.add(is);
            }
        }

        return result;
    }
}