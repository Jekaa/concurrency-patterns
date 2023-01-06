# concurrency-patterns
Паттерны многопоточности:
* Active object
* CyclicBarrier
* Double checking locking
* Leader followers
* Monitor object
* Reactor
* CountDownLatch
* Exchanger
* Phaser
* Semaphore

Очереди (конспект):
* ArrayBlockingQueue — очередь, реализующая классический кольцевой буфер;
* LinkedBlockingQueue — односторонняя очередь на связанных узлах;
* LinkedBlockingDeque — двунаправленная очередь на связанных узлах;
* SynchronousQueue — блокирующую очередь без емкости (операция добавления одного потока находится в ожидании соответствующей операции удаления в другом потоке);
* LinkedTransferQueue — реализация очереди на основе интерфейса TransferQueue;
* DelayQueue — неограниченная блокирующая очередь, реализующая интерфейс Delayed;
* PriorityBlockingQueue — реализация очереди на основе интерфейса PriorityQueue.
