import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CountingQueue<T> {

    private final BlockingQueue<T> queue;
    private final AtomicInteger enqueued = new AtomicInteger(0);
    private final AtomicInteger working = new AtomicInteger(0);
    private final AtomicInteger worked = new AtomicInteger(0);
    private final AtomicBoolean finishedEnqueue = new AtomicBoolean(false);

    public CountingQueue() {
        this(new LinkedBlockingQueue<T>());
    }
    public CountingQueue(BlockingQueue<T> queue) {
        this.queue = queue;
    }


    public synchronized void enqueue(T obj) {
        try {
            queue.put(obj);
            enqueued.incrementAndGet();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }

    public synchronized void finishedEnqueue() {
        finishedEnqueue.set(true);
    }

    public boolean allFinished() {
        return finishedEnqueue.get()
            && enqueued.get() == working.get()
            && working.get() == worked.get();
    }

    public QueueEntry<T> poll() {
        T obj = queue.poll();
        if (obj != null) {
            working.incrementAndGet();
            return new QueueEntry<T>(obj);
        }
        return null;
    }

    public String status() {
        return "Q(" + enqueued + "," + working + "," + worked + "," + finishedEnqueue + ")";
    }

    public class QueueEntry<T> implements AutoCloseable {
        private final T obj;
        private boolean finished = false;
        private QueueEntry(T obj) {
            this.obj = obj;
        }

        public Optional<T> get() {
            return Optional.ofNullable(obj);
        }

        @Override
        public void close() {
            if (!finished && obj != null) {
                finished = true;
                worked.incrementAndGet();
            }
        }
    }
}
