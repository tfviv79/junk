
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountingQueueTest {

    private static final long pwait = 200;
    private static final long cwait = 100;
    private static final long wwait = 300;

    public static void main(String[] args) {
        CountingQueue<Integer> queue = new CountingQueue<>();

        ExecutorService pool1 = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService pool2 = Executors.newVirtualThreadPerTaskExecutor();

        pool1.submit(producer(queue));
        pool2.submit(consumer(queue));

        while (true) {
            sleep(100);
            if (queue.allFinished()) {
                info("all finished");
                break;
            }
        }
        // sleep(5000);
        info("end status %s", queue.status());
    }


    private static Callable<Integer> producer(final CountingQueue<Integer> queue) {
        return () -> {
            info("producer working");
            int max = 10;
            for (int i=0; i<max; i++) {
                sleep(pwait);
                queue.enqueue(i);
            }
            queue.finishedEnqueue();
            info("producer worked");
            return 0;
        };
    }

    private static void worker(Optional<Integer> no) {
        info("      worker do   %s --> %s", Thread.currentThread(), no);
        sleep(wwait);
        info("      worker done %s --> %s", Thread.currentThread(), no);
    }

    private static Callable<Integer> consumer(final CountingQueue<Integer> queue) {
        return () -> {
            ExecutorService workerPool = Executors.newVirtualThreadPerTaskExecutor();
            info("consumer working");
            while (!queue.allFinished()) {
                final var no = queue.poll();
                if (no != null) {
                    workerPool.submit(() -> {
                        worker(no.get());
                        no.close();
                    });
                }
                sleep(cwait);
                info("    queue state = %s", queue.status());
            }
            info("consumer worked");
            return 1;
        };
    }


    private static void info(String fmt, Object ... args) {
        System.out.println(String.format(fmt, args));
    }

    private static void sleep(long sleepInMs) {
        try {
            Thread.sleep(sleepInMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
}
