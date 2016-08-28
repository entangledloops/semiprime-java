package com.snd.semiprime.client;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.snd.semiprime.Log;
import com.snd.semiprime.server.Server;

import java.net.InetAddress;

/**
 * @author snd
 * @since August 27, 2016
 */

public class HazelcastClient
{
  final Config config;
  final HazelcastInstance hazelcast;

  public HazelcastClient()
  {
    try
    {
      final InetAddress address = InetAddress.getByName(Server.DEFAULT_HOST);
      System.out.println( address.getHostAddress() );

      config = new Config();
      config.setInstanceName( "client" );
      config.getGroupConfig().setName( "semiprime" ).setPassword( "servebeer" );

      config.getNetworkConfig()
          //.setPort(Server.DEFAULT_PORT )
          //.setPortAutoIncrement( false )
          .setReuseAddress( true )
          .getJoin()
            .getMulticastConfig()
              .setEnabled( true );

      config.getNetworkConfig()
          .getJoin()
            .getTcpIpConfig()
              .setRequiredMember( address.getHostAddress() )
              .setEnabled( false );

      hazelcast = Hazelcast.newHazelcastInstance( config );
    }
    catch (Throwable t)
    {
      Log.e(t);
      throw new NullPointerException("client setup failure");
    }
  }
}
