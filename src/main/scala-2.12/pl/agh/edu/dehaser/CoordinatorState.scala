package pl.agh.edu.dehaser

import akka.actor.ActorRef

sealed trait CoordinatorState

case object Idle extends CoordinatorState

case object Master extends CoordinatorState

case object ChunkProcessing extends CoordinatorState


sealed trait CoordinatorData

case object Uninitialized extends CoordinatorData


case class ProcessData(subContractors: Set[ActorRef],
                       rangeConnector: RangeConnector, workDetails: WorkDetails,
                       rangeToCheck: BigRange, iterator: BigRangeIterator,
                       parent: ActorRef, masterCoordinator: ActorRef, aggregator: ActorRef) extends CoordinatorData


//case class MasterData(subContractors: Set[ActorRef],
//                      rangeConnector: RangeConnector, workDetails: WorkDetails,
//                      rangeToCheck: BigRange, iterator: BigRangeIterator,
//                      client: ActorRef) extends CoordinatorData


//
//private val slaves = mutable.Set.empty[ActorRef]
//private val subContractors = mutable.Set[ActorRef]()
//private var reportAggregator: ActorRef = _
//private var masterCoordinator: ActorRef = _
//private var rangeConnector: RangeConnector = _
//private var workDetails: WorkDetails = _
//private var rangeToCheck: BigRange = _
//private var iterator: BigRangeIterator = _
//private var client: ActorRef = _
//private val queue = context.actorSelection("/user/queue")
//
//
//
//
//
//