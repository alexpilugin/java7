/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package val.pps.processor2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Alex
 */
public class ValPPSProcessorTest {
    public static void main(String[] args) {
        
        if(args.length > 0) {Starter.go(args);
            System.exit(0);
        }
        
        String [] sa = {"4 1  707  8  1  20  1  5  7", 
                        "1  200  8  1  4  1  5  4 ", 
                        "1  707  9  1  200  9 6"};
        Starter.go(sa);
    }
}

class Command{
    private Integer code;
    private Integer arg;
    
    public Command(Integer code, Integer arg){
        this.code = code;
        this.arg = arg;
    }
    public Integer getCode(){
        return code;
    }
    public Integer getArg(){
        return arg;
    }
}


class Task {
    int id;
    int result;
    boolean isDone;
    final Thread thread;
    
    public Task(int id, int result, final Thread currThread){
        this.id = id;
        this.result = result;
        this.isDone = false;
        this.thread = currThread;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj instanceof Task)) return false;
        Task one = (Task)obj;
        
        if ((this.id != one.id) || this.result != one.result ){
            return false;
        }else{ return true; }
    }
}

class CommandSequenceParser{
    private ArrayList<Command> commandsList = new ArrayList<Command>();
    
    public void ParseCodeSequence(String s){
        if (s == null) return;
        Scanner scan = new Scanner(s);
        
        while(scan.hasNext()){
            String StrCode = scan.next();
            String StrArg = null;
            
            if (StrCode.equals("1")){
               StrArg = scan.next();
            }
            Integer i1 = null;
            Integer i2 = null;
            i1 = Integer.parseInt(StrCode);
            if (StrArg != null){
              i2 = Integer.parseInt(StrArg);
            }
            
            if (StrCode != null){
                Command cmd = new Command(i1,i2);
                commandsList.add(cmd);
            }
    
            }
        
    } 
    public ArrayList<Command> getCommands(){
        return commandsList;
    }
    
}


class TaskWorksList{
    private static ArrayList<Task> ID_list = new ArrayList<Task>();
    
    public synchronized void addTask(Task task){
        ID_list.add(task);
        this.notifyAll();
    }
    
    public Object[] toArray(){
     return  ID_list.toArray();
    }
    
    public synchronized void setTask(Task task){
        if (!ID_list.contains(task)) {
            ID_list.add(task);
        }else{
            for(Task temp : ID_list){
                if (temp.id == task.id){ 
                    temp.result = task.result;
                    temp.isDone = task.isDone;
                    if (temp.isDone) this.notifyAll();
                }
            }
        }
    }
    
    public synchronized Task getTask(int id){
        Task result = null;
        
        for (Task t : ID_list){
            if (t.id == id){
                result = t;
            }
        }
        return result;
    } 
    
    public boolean isEmpty(){
        return ID_list.isEmpty();
    }
    
    public int getSize(){
        return ID_list.size();
    }
    
}

class Runner implements Runnable{
    private ArrayList<Command> commands;
    private LinkedList<Integer> dataQueue = new LinkedList<Integer>();
    private final TaskWorksList twl;
    
    
    Runner(ArrayList<Command> commands, final TaskWorksList wl){
        this.commands = commands;
        this.twl = wl;
    } //constructor
    
    @Override
    public void run() {
        Task currentTask = new Task(twl.getSize(),0, Thread.currentThread());
        twl.addTask(currentTask);
        
        if (!commands.isEmpty()){
            
            for (Command com: commands){
                switch(com.getCode()){
                    /** Pushes the single argument onto the stack */
                    case 1 : {
                        Integer i = com.getArg();
                        dataQueue.addFirst(i);
                        //System.out.println(Thread.currentThread().getName() + " Runner:code 1");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }
                    /** Removes the value at the top of the stack */
                    case 2 : {
                        dataQueue.poll();
                        //System.out.println(Thread.currentThread().getName() + "Runner:code 2");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }
                    /** Duplicates the value at the top of the stack */
                    case 3 : {
                        Integer i = dataQueue.peek();
                        dataQueue.offer(i);

                        //System.out.println(Thread.currentThread().getName() + "Runner:code 3");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }
                    /** Removes the top two numbers from the stack, 
                        adds them and places the result on the stack. */
                    case 4 : {
                        Integer a = dataQueue.poll();
                        Integer b = dataQueue.poll();
                        if (a == null) a = 0;
                        if (b == null) b = 0;
                        Integer c = a + b;
                        dataQueue.offer(c);
                        currentTask.result = c;
                        //System.out.println(Thread.currentThread().getName() + "Runner:code 4");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }
                    /** Removes the top two numbers from the stack , 
                      * Subtracts the top number on the stack from 
                      * the second number on the stack, 
                      * placing the result on the stack. */
                    case 5 : {
                        Integer a = dataQueue.poll();
                        Integer b = dataQueue.poll();
                        if (a == null) a = 0;
                        if (b == null) b = 0;
                        Integer c = b - a;
                        dataQueue.offer(c);
                        currentTask.result = c;
                        //System.out.println(Thread.currentThread().getName() + "Runner:code 5");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }  
                    /** Removes the top two numbers from the stack, 
                      * multiplies them 
                      * and places the result on the stack. */
                    case 6 : {
                        Integer a = dataQueue.poll();
                        Integer b = dataQueue.poll();
                        if (a == null) a = 0;
                        if (b == null) b = 0;
                        Integer c = b * a;
                        dataQueue.offer(c);
                        currentTask.result = c;
                        //System.out.println(Thread.currentThread().getName() + "Runner:code 6");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }
                    /** Removes the top two numbers from the stack , 
                      * Divides the top number on the stack from 
                      * the second number on the stack,  
                      * placing the result on the stack. */
                    case 7 : {
                        Integer a = dataQueue.poll();
                        Integer b = dataQueue.poll();
                        if (a == null) a = 0;
                        if (b == null) b = 0;
                        Integer c;
                        if(a != 0){
                            c = b / a;
                        }else{ c = 0; }
                        dataQueue.offer(c);
                        currentTask.result = c;
                        //System.out.println(Thread.currentThread().getName() + "Runner:code 7");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }                        
                    /** Take the top value on the stack, 
                      * removes it and assigns it as an ID to the whole routine.
                      * Once given an ID other tasks can reference the result. */
                    case 8 : {
                        Integer id = dataQueue.poll();
                        currentTask.id = id;
                            synchronized(twl){
                               try{
                                  twl.notifyAll();
                                  Thread.currentThread().sleep(100);
                               }catch(Exception e){ 
                                  e.printStackTrace();
                               }
                            }
                        //System.out.println(Thread.currentThread().getName() + "Runner:code 8");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    } 
                    /** Takes the top number on the stack, 
                      * removes it and uses it as an identifier 
                      * to retrieve the result of the task with that identifier, 
                      * waiting if necessary for the dependant task to complete 
                      * if not already complete,
                      * placing the result on the stack. */
                    case 9 : {
                        Task ta = null;
                        Integer id = null;
                        boolean found = false;
                        
                            /** *
                             * Here there is a danger.
                             * How long does it need to wait the task
                             * (another thread of execution) with particular id?
                             * Does it exist indeed at all?
                             */
                        if(this.dataQueue.isEmpty()) return;
                        
                        if(this.dataQueue.size() != 0){
                            id = dataQueue.poll();
                            int index = 0;
                            long sleepTime = 200;
                            do{
                                ta =  twl.getTask(id);
                                try {
                                    if(ta == null)
                                        Thread.currentThread().sleep(sleepTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }while(ta == null) ;
                        }
                                try {
                                    if (ta.thread.isAlive())
                                        ta.thread.join();
                                   if (ta.isDone){ 
                                       dataQueue.offer(ta.result);
                                       currentTask.result = ta.result;
                                   }
                                } catch (InterruptedException e) {
                                     e.printStackTrace();
                                }

                        //System.out.println(Thread.currentThread().getName() + "Runner:code 9");
                        //System.out.println("id: " + currentTask.id + " res: " + currentTask.result);
                        break;
                    }
                        
                } //switch ends
            } //for loop ends

        }
        if (!dataQueue.isEmpty()) {
            currentTask.isDone = true;
        }
        twl.setTask(currentTask);
        try{
            Thread.currentThread().sleep(100);
        }catch(Exception e){
            e.printStackTrace();
            
        }
        System.out.println(Thread.currentThread().getName() + " : " + currentTask.result);
        
    }

}

class Starter{
    private static String n = null;
    private static final List<Thread> threadList = new ArrayList<Thread>();
    
    public static void go(String taskSequence){
        CommandSequenceParser parser = new CommandSequenceParser();
        parser.ParseCodeSequence(taskSequence);
        
        TaskWorksList tWL = new TaskWorksList();
        Runner r = new Runner(parser.getCommands(), tWL);
        
        Thread tr = new Thread(r);
        if (n != null)tr.setName(n);
        threadList.add(tr);
        tr.start();
    }
    
    public static void go(String [] args){
        if(args.length >0){
            for(String s : args)
                go(s);
        }
    }
    
    public static void setName(String name){
        n = name;
    }
    /** for debugging only */
    public List<Thread> getThreadList(){
        return  threadList;
    }
}