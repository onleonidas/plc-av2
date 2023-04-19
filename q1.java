
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Recebe a quantidade de aviões que irão decolar
        System.out.println("Digite a quantidade de aviões que irão decolar:");
        int numAvioesDecolar = sc.nextInt();

        // Recebe as horas de saída de cada avião que irá decolar
        int[] horariosDecolagem = new int[numAvioesDecolar];
        for (int i = 0; i < numAvioesDecolar; i++) {
            System.out.println("Digite a hora de saída (em milissegundos) do avião " + (i+1) + ":");
            horariosDecolagem[i] = sc.nextInt();
        }

        // Recebe a quantidade de aviões que irão pousar
        System.out.println("Digite a quantidade de aviões que irão pousar:");
        int numAvioesPousar = sc.nextInt();

        // Recebe as horas de chegada de cada avião que irá pousar
        int[] horariosPouso = new int[numAvioesPousar];
        for (int i = 0; i < numAvioesPousar; i++) {
            System.out.println("Digite a hora de chegada (em milissegundos) do avião " + (i+1) + ":");
            horariosPouso[i] = sc.nextInt();
        }

        // Recebe a quantidade de pistas disponíveis no aeroporto
        System.out.println("Digite a quantidade de pistas disponíveis no aeroporto:");
        int numPistas = sc.nextInt();

        //the pistas
        Pista[] pistas = new Pista[numPistas];
        boolean[] pistasBolean = new boolean[numPistas];
        for(int i = 0; i < numPistas; i++){
            pistas[i] = new Pista();
            pistasBolean[i] = false;
        }



        //the lock
        Lock lock = new ReentrantLock(true);

        //the time the program is starting
        long startTime = System.currentTimeMillis();

        //the condition to avoid redundant search
        Lock condicaoPistaCheiaLock = new ReentrantLock();
        Condition condicaoPistaCheia = condicaoPistaCheiaLock.newCondition();
        //its lock

        //for those who will take off
        Aviao[] avioesD = new Aviao[numAvioesDecolar];
        for (int i = 0; i < avioesD.length; i++) {
            //time
            int hora = horariosDecolagem[i];
            //this airplane is going to decolou
            String acao = "decolou";

            //create a new aviao
            avioesD[i] = new Aviao(hora,acao,startTime,pistas,lock,pistasBolean,condicaoPistaCheiaLock,condicaoPistaCheia);

        }

        //for those who will land
        boolean[] avioesPB = new boolean[numAvioesDecolar];
        Aviao[] avioesP = new Aviao[numAvioesPousar];
        for (int i = 0; i < avioesP.length; i++) {
            //time
            int hora = horariosPouso[i];
            //this airplane is going to pousou
            String acao = "pousou";
            //create a new aviao
            avioesP[i] = new Aviao(hora,acao,startTime,pistas,lock,pistasBolean,condicaoPistaCheiaLock,condicaoPistaCheia);

        }

        for (int i = 0; i < avioesD.length; i++) {
            Thread t = new Thread(avioesD[i]);
            t.start();
        }
        for (int i = 0; i < avioesP.length; i++) {
            Thread t = new Thread(avioesP[i]);
            t.start();
        }

    }
}


public class Pista {
    private boolean isBeingUsed = false;

    public synchronized boolean tryUse() {
        if (this.isBeingUsed) {
            return false;
        } else {
            this.isBeingUsed = true;
            return true;
        }
    }

    public synchronized void setBeingUsed(boolean use) {
        this.isBeingUsed = use;
    }

    public synchronized void usar() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Aviao implements Runnable {
    private int hora;
    private String acao;
    long startTime;
    Pista[] arrPista;
    Lock ck;
    boolean[] arrPistaUse;
    int arrPistaLength;
    Lock pistaCheiaLock;
    Condition pistaCheia;

    public Aviao(int hora, String acao, long startTime, Pista[] arrPista, Lock ck, boolean[] arrPistaUse, Lock pistaCheiaLock, Condition pistaCheia) {
        this.hora = hora;
        this.acao = acao;
        this.startTime = startTime;
        this.arrPista = arrPista;
        this.ck = ck;
        this.arrPistaUse = arrPistaUse;
        this.arrPistaLength = arrPista.length;
        this.pistaCheiaLock = pistaCheiaLock;
        this.pistaCheia = pistaCheia;
    }

    public synchronized void run() {
        long currTime = System.currentTimeMillis();
        while ((currTime - this.startTime) < hora) {
            currTime = System.currentTimeMillis();
        }
        //time to consume
        //run get the lock for the array
        //maybe the arrPista needs to be defined prior to avoid problems


        //this lock is about the right to search for a pista it needs to be a ReentrantLock lock
        this.ck.lock();
        //search for until you find if
        int i = 0;
        while (i < arrPista.length) {
            //System.out.println(this.acao + " das " + this.hora + " esta tentando acessar a pista: " + i);

            //try to use the pista
            if (!this.arrPistaUse[i]) {
                //now another one can search for a pista
                this.arrPistaUse[i] = true;
                this.ck.unlock();

                //enjoy it
                this.arrPista[i].usar();

                //another can use this pista now
                this.arrPistaUse[i] = false;
                //System.out.println("o voo " + this.hora + " liberou a pista");

                //signal that one can search for a pista
                this.pistaCheiaLock.lock();
                this.pistaCheia.signalAll();
                this.pistaCheiaLock.unlock();
                //System.out.println("o voo " + this.hora + " liberou o lock");

                //this.arrPista[i].setBeingUsed(false);

                //print
                long passedTime = System.currentTimeMillis() - this.startTime;
                System.out.println("O aviao " + this.acao + ", o horario esperado era de " + this.hora + ", mas ele so " + this.acao + " as " + passedTime + " usando a pista: " + i + "\no que siginifica um atrado de: " + (passedTime - this.hora));
                //finish this loop
                break;
            }
            i++;
            if (i == arrPistaLength) {
                try {
                    this.pistaCheiaLock.lock();
                    //System.out.println("esperando alguem liberar");
                    this.pistaCheia.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    i = 0;
                    this.pistaCheiaLock.unlock();
                }
            }
        }
    }
}
