/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package l2s.authserver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;
//import java.util.logging.Logger;

import org.apache.log4j.Logger;

public class RouterTable
{
    protected static Logger _log = Logger.getLogger(RouterTable.class.getName());
    private final String[]/*[]*/ _iptable = new String/*[255]*/[255];
    private int _iptable_counts = 0;
    private static RouterTable _instance;

    public static RouterTable getInstance()
    {
        if (_instance == null)
            _instance = new RouterTable();
        return _instance;
    }

    public RouterTable()
    {}

    public String getOutIp(final String incoming_ip)
    {
        if (/*id == 0 || */incoming_ip == null || incoming_ip == ""
                || _iptable_counts/*[id] */ == 0)
            return null;

        for (int i = 0; i < _iptable_counts/*[id]*/; i++)
        {
            if (_iptable/*[id]*/[i] == null)
                continue;

            final StringTokenizer st = new StringTokenizer(_iptable/*[id]*/[i],
                    "\t");
            if (st.hasMoreTokens())
            {
                final String input_ip_table = st.nextToken();
                final String output_ip_table = st.nextToken();
                if (incoming_ip.startsWith(input_ip_table)
                        || incoming_ip.equals(input_ip_table) || input_ip_table.equals("0.0.0.0"))
				{
					_log.info("inIP=" + incoming_ip + ", outIP=" + output_ip_table);
					return output_ip_table;
				}
            }
        }
		_log.info("inIP=" + incoming_ip + ", outIP= NULL");
        return null;
    }

    public void readRouterTable()
    {
        final File file = new File("config/iptables.properties");
        if (file.exists())
        {
            LineNumberReader lnr = null;
            int ips_count = 0;
            try
            {
                String line = null;
                lnr = new LineNumberReader(new FileReader(file));
                while ((line = lnr.readLine()) != null)
                {
                    if (line.trim().length() == 0 || line.startsWith("#")
                            || line.startsWith("\n") || line.startsWith("\r"))
                        continue;

                    _iptable/*[id]*/[ips_count] = line;
                    ips_count++;
                }

            }
            catch (final IOException e1)
            {}
            finally
            {
                try
                {
                    lnr.close();
                }
                catch (final Exception e2)
                {}
            }
            _iptable_counts/*[id]*/ = ips_count;
            _log.info("Router : Loaded " + ips_count
                    + " ip`s to route table for gameservers" );
        }
        else
            _log.info("Router: file config/iptables.properties is not exist. Routings disabled");
    }
}
