package com.snd.semiprime.server;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.snd.semiprime.Log;

/**
 * @author snd
 * @since August 27, 2016
 */

public class HazelcastServer
{
  final Config config;
  final HazelcastInstance hazelcast;

  public HazelcastServer()
  {
    try
    {
      config = new Config();
      config.setInstanceName( Server.DEFAULT_HOST );
      config.getGroupConfig().setName( "semiprime" ).setPassword( "servebeer" );
      config.getNetworkConfig()
          .setReuseAddress( true )
          .setPort(Server.DEFAULT_PORT )
          .setPortAutoIncrement( false );

      hazelcast = Hazelcast.newHazelcastInstance( config );
    }
    catch (Throwable t)
    {
      Log.e(t);
      throw new NullPointerException("server setup failure");
    }
  }

}
