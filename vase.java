import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class vase {

    // Global integer to keep track of the number of guests.
    static int guests = 10;

    static class crystalVase implements Runnable {
    
        // Define variables to keep track of room availability, list of guests who have entered the room, and handle locking/randomness
        static boolean available = true;
        static HashSet<Integer> guestList = new HashSet<>();
        int guestID;
        private static Lock enteredRoom = new ReentrantLock();
        private Random random = new Random();
    
        crystalVase(int guestID){
            this.guestID = guestID;
        }
    
        @Override
        public void run() {
            // Thread sleep to ensure randomness in guests observing the vase
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
            
            while (guestList.size() < guests) {
                // Lock when a guest enters the room
                enteredRoom.lock();
                System.out.println("Guest " + guestID + " is near the vase room");
                try {
                    // If current guest hasn't viewed vase and it is available, view the vase for random time and add to guestList so that they cannot reenter
                    if (!guestList.contains(guestID) && available){
                        available = false;
                        guestList.add(guestID);
                        System.out.println("Guest " + guestID + " Saw the Vase");
                        Thread.sleep(random.nextInt(1000));
                        available = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    // Unlock room when guest has left.
                    enteredRoom.unlock();
                }
            }
        }
    }
        
    public static void main(String[] args) throws InterruptedException {
        // executor service to activate threads
        ExecutorService executor = Executors.newFixedThreadPool(guests);
        Random random = new Random();
        for (int i = 0; i < guests; i++){
            executor.execute(new crystalVase(i));
            // Thread sleep to promote randomness among threads running
            Thread.sleep(random.nextInt(1000));
        }

        executor.shutdown();
    }
}