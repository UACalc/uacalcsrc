package org.uacalc.ui.tm;


import java.util.concurrent.*;

/**
 * BackgroundTask
 * <p/>
 * Background task class supporting cancellation, completion notification, 
 * and progress notification.
 *
 * @author Brian Goetz and Tim Peierls
 */

public abstract class BackgroundTask <V> implements Runnable, Future<V> {
  
  //possibly add UNKNOWN_ERROR  
  public enum Status { RUNNING, OUT_OF_MEMORY, CANCELLED, DONE}  
  
  protected volatile Status status = Status.RUNNING;
  
  protected ProgressReport report;
  
  private final FutureTask<V> computation = new Computation();

  public BackgroundTask(ProgressReport report) {
    this.report = report;
  }
  
  private class Computation extends FutureTask<V> {
  
    static final int memReserve = 1048576;
    byte[] buf = new byte[memReserve];
    
    public Computation() {
      super(new Callable<V>() {
        public V call() throws Exception {
          return BackgroundTask.this.compute();
        }
      });
    }

    protected final void done() {
      buf = null;
      GuiExecutor.instance().execute(new Runnable() {
        public void run() {
          V value = null;
          Throwable thrown = null;
          boolean cancelled = false;
          try {
            value = get();
          } 
          catch (ExecutionException e) {
            thrown = e.getCause();
          } 
          catch (CancellationException e) {
            cancelled = true;
          } 
          catch (InterruptedException consumed) {
          } 
          finally {
            buf = null;
            final boolean outOfMemory = thrown instanceof OutOfMemoryError;
            if (outOfMemory) status = Status.OUT_OF_MEMORY;
            else if (cancelled) status = Status.CANCELLED;
            else status = Status.DONE;
            onCompletion(value, thrown, cancelled, outOfMemory);
          }
        };
      });
    }
  }

  protected void setProgressReport(ProgressReport pr) {
    report = pr;
  }
  
  public ProgressReport getProgressReport() { return report; }
  
  /*
  protected void setProgress(final int current, final int max) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        onProgress(current, max);
      }
    });
  }
  */

  // Called in the background thread
  protected abstract V compute() throws Exception;

  // Called in the event thread
  protected void onCompletion(V result, Throwable exception,
                boolean cancelled, boolean outOfMemory) {
  }
  
  public Status getStatus() { return status; }
  public void setStatus(Status v) { status = v; }

  protected void onProgress(int current, int max) {
  }

  // Other Future methods just forwarded to computation
  public boolean cancel(boolean mayInterruptIfRunning) {
    return computation.cancel(mayInterruptIfRunning);
  }

  public V get() throws InterruptedException, ExecutionException {
    return computation.get();
  }

  public V get(long timeout, TimeUnit unit)
      throws InterruptedException,
      ExecutionException,
      TimeoutException {
    return computation.get(timeout, unit);
  }

  public boolean isCancelled() {
    return computation.isCancelled();
  }

  public boolean isDone() {
    return computation.isDone();
  }

  public void run() {
    computation.run();
  }
}




