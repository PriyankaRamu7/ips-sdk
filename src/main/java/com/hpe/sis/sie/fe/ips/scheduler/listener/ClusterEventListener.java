package com.hpe.sis.sie.fe.ips.scheduler.listener;

import akka.actor.UntypedAbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;

public class ClusterEventListener extends UntypedAbstractActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Cluster cluster = Cluster.get(getContext().system());
	SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
	private static int count = 3;

	public void preStart() {
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberUp.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.LeaderChanged.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.UnreachableMember.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberRemoved.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberEvent.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberExited.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberJoined.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberLeft.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.MemberWeaklyUp.class);
		this.cluster.subscribe(getSelf(), ClusterEvent.ReachableMember.class);
	}

	public void postStop() {
		this.cluster.unsubscribe(getSelf());
	}

	public void onReceive(Object message) {
		this.log.info("###################  CLUSTEREVENTLISTENER received message ################### : " + message);

		if (message instanceof ClusterEvent.MemberUp) {
			ClusterEvent.MemberUp mUp = (ClusterEvent.MemberUp) message;
			this.log.info("################### Member is Up: {}################### ", mUp.member());

		} else if (message instanceof ClusterEvent.UnreachableMember) {
			ClusterEvent.UnreachableMember mUnreachable = (ClusterEvent.UnreachableMember) message;
			this.log.info("################### Member detected as unreachable: {}################### ",
					mUnreachable.member());

			String host = mUnreachable.member().address().host().toString();
			String portStr = mUnreachable.member().address().port().toString();
			String ip = host.substring(host.indexOf("(") + 1, host.indexOf(")"));
			int port = Integer.parseInt(portStr.substring(portStr.indexOf("(") + 1, portStr.indexOf(")")));

			this.log.info("################### Host IP and Port detected as unreachable: {}################### "+ ip + "  " + port);
			this.log.info("################### Going to ping host " + mUnreachable.member() + " to check whether it is reachable again ################### ");

			boolean isReachable = false;
			InetSocketAddress endPoint = null;
			Socket socket = null;
			for (int i = 1; i <= count; i++) {
				try {
					socket = new Socket();
					endPoint = new InetSocketAddress(ip, port);
					this.log.info("################### Checking whether the host is reachable again for " + i
							+ " time ################### "+ ip + ":" + port);

					socket.connect(endPoint, 3000);
					isReachable = true;
					this.log.info("################### isReachable? : {}################### ", isReachable);

					if (isReachable)
						break;

				} catch (IOException ioe) {
					this.log.info("Failed to connect to " + ip + ":" + port);

				} catch (IllegalBlockingModeException ibme) {
					this.log.info("Failed to connect to " + ip + ":" + port);

				} catch (IllegalArgumentException iae) {
					this.log.info("Failed to connect to " + ip + ":" + port);

				} finally {
					if (socket != null)
						try {
							socket.close();
						} catch (IOException ioex) {
						}

				}
				if(i != count) {
					try {
						Thread.sleep(5000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (!isReachable) {
				this.log.info("################### Member is detected as not reachable. IsReachable? " + isReachable + " ################### ");
				this.cluster.down(mUnreachable.member().address());
				this.log.info("################### Member is forced to down state {}################### " + ip);
			}
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ipsActorSystem.clusterSingletonProxy.tell(mUnreachable.member().address().toString(), getSelf());

		} else if (message instanceof ClusterEvent.MemberRemoved) {
			ClusterEvent.MemberRemoved mRemoved = (ClusterEvent.MemberRemoved) message;
			this.log.info("################### Member is Removed: {}################### ", mRemoved.member());
		}

		else if (message instanceof ClusterEvent.LeaderChanged) {
			ClusterEvent.LeaderChanged leader = (ClusterEvent.LeaderChanged) message;
			this.log.info("################### leader is changed : {}################### ", leader.getLeader());

		} else if (message instanceof ClusterEvent.MemberExited) {
			ClusterEvent.MemberExited memberExited = (ClusterEvent.MemberExited) message;
			this.log.info("################### Member Exited : {}################### ", memberExited.member());

		} else if (message instanceof ClusterEvent.MemberJoined) {
			ClusterEvent.MemberJoined memberJoined = (ClusterEvent.MemberJoined) message;
			this.log.info("################### Member Joined : {}################### ", memberJoined.member());

		} else if (message instanceof ClusterEvent.MemberLeft) {
			ClusterEvent.MemberLeft memberLeft = (ClusterEvent.MemberLeft) message;
			this.log.info("################### Member Left : {}################### ", memberLeft.member());

		} else if (message instanceof ClusterEvent.MemberWeaklyUp) {
			ClusterEvent.MemberWeaklyUp memberWeaklyUp = (ClusterEvent.MemberWeaklyUp) message;
			this.log.info("################### Member WeaklyUp : {}################### ", memberWeaklyUp.member());

		} else if (message instanceof ClusterEvent.ReachableMember) {
			ClusterEvent.ReachableMember reachableMember = (ClusterEvent.ReachableMember) message;
			this.log.info("################### Reachable Member : {}################### ", reachableMember.member());

		} else {
			unhandled(message);
		}

	}
}
