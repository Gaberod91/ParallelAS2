import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class cupcake {

    static int guests = 10;

    static class cupcakeMaze implements Runnable {
      
        // Define our variables to flag if a cupcake is found, increment the number of cupcakes consumed, and handle locking/randomness
        static boolean foundCupcake = true;
        static int cupcakesConsumed = 0;
        static boolean[] consumedCupcake = new boolean[guests];
        private static Lock mazeLock = new ReentrantLock();
        private Random random = new Random();
        int guestID;

        cupcakeMaze(int guestID) {
            this.guestID = guestID;
        }
    
        @Override
        public void run() {
            
            while (cupcakesConsumed < guests) {
                // Lock maze once a guest has entered.
                mazeLock.lock();
                System.out.println("Guest " + guestID + " has entered the maze");
                try {
                    // When guest reaches the cupcake, if it is there & they haven't eaten it yet, they take the cupcake.
                    if (!consumedCupcake[guestID] && foundCupcake){
                        foundCupcake = false;
                        consumedCupcake[guestID] = true;
                        System.out.println("Guest " + guestID + " Found the Cupcake: " + cupcakesConsumed);
                    }
                    // Establish guest 0 as the "communicator" if there's no cupcake, guest 0 knows to increment cupcakesConsumed and requests another. 
                    if (guestID == 0 && !foundCupcake){
                        cupcakesConsumed += 1;
                        foundCupcake = true;
                        System.out.println("Guest 0 Found the Cupcake: " + cupcakesConsumed);
                    }
                } finally {
                    // Unlock maze when the guest leaves the maze
                    mazeLock.unlock();
                }
                
                // Sleep the thread to ensure randomness in guests entering the maze.
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
        
    public static void main(String[] args) {
        // executor service to activate threads
        ExecutorService executor = Executors.newFixedThreadPool(guests);

        for (int i = 0; i < guests; i++){
            executor.execute(new cupcakeMaze(i));
        }

        executor.shutdown();
    }
}