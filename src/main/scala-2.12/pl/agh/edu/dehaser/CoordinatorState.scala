package pl.agh.edu.dehaser

import akka.actor.ActorRef

sealed trait CoordinatorState

case object Idle extends CoordinatorState

case object Master extends CoordinatorState

case object ChunkProcessing extends CoordinatorState


sealed trait CoordinatorData

case object Uninitialized extends CoordinatorData


case class ProcessData(subContractors: Set[ActorRef], workDetails: WorkDetails, iterator: BigRangeIterator,
                       parent: ActorRef, masterCoordinator: ActorRef, aggregator: ActorRef) extends CoordinatorData

