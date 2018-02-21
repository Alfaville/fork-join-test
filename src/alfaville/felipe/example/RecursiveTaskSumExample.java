package alfaville.felipe.example;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RecursiveTaskSumExample extends RecursiveTask<Long> {

    private static final Logger LOGGER = Logger.getAnonymousLogger();
    
	private static final int THRESHOLD = 5;

	private List<Long> data;

	public RecursiveTaskSumExample(List<Long> data) {
		this.data = data;
	}

	@Override
	protected Long compute() {
		if (this.data.size() < THRESHOLD) { // portion of the work is small enough?
			return this.processing();
		} else { // split my work into two pieces
			LOGGER.info("split my work into two pieces");
			final int HALF = this.data.size() / 2;
			RecursiveTaskSumExample firstTask = new RecursiveTaskSumExample(this.data.subList(0, HALF));
			RecursiveTaskSumExample secondTask = new RecursiveTaskSumExample(this.data.subList(HALF, this.data.size()));
			
			firstTask.fork(); //queue the first task
//			secondTask.compute(); //compute the second task
//			firstTask.join();// wait for the first task result
			
	           // Or simply call
            //ForkJoinTask.invokeAll(firstSubtask, secondSubtask); or ForkJoinTask.invokeAll(createSubtasks());
			
			return secondTask.compute() + firstTask.join();
		}
	}

	private Long processing() {
		long sumValues = 0;
        for (Long l: this.data) 
        		sumValues += l;        
		LOGGER.info(String.format("This result - (%d) - was processed by %s", sumValues, Thread.currentThread().getName()));
		return sumValues;
	}

    public static void main(String[] args) {
        Random random = new Random();

        List<Long> data = random
                .longs(11, 1, 5)
                .boxed()
                .collect(Collectors.toList());

        ForkJoinPool pool = new ForkJoinPool();
        System.out.println("Pool parallelism: " + pool.getParallelism());
        RecursiveTaskSumExample task = new RecursiveTaskSumExample(data);
        System.out.println("Sum: " + pool.invoke(task));
    }
	
	
}
