import java.util.*;
import java.util.concurrent.*;

class Colmeia {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        int operarios = scanner.nextInt();
        int tarefas = scanner.nextInt();
        scanner.nextLine(); // Consumir a linha restante

        List<Tarefa> listaTarefas = new ArrayList<>();

        for (int i = 0; i < tarefas; i++) {
            String[] linha = scanner.nextLine().split(" ");
            int id = Integer.parseInt(linha[0]);
            int tempo = Integer.parseInt(linha[1]);
            List<Integer> dependencias = new ArrayList<>();
            for (int j = 2; j < linha.length; j++) {
                dependencias.add(Integer.parseInt(linha[j]));
            }
            listaTarefas.add(new Tarefa(id, tempo, dependencias));
        }
        scanner.close();


        ExecutorService executor = Executors.newFixedThreadPool(operarios);
        Queue<Tarefa> fila = new LinkedList<>(listaTarefas);

        while (!fila.isEmpty()) {
            Tarefa tarefa = fila.poll();
            if (tarefa.podeIniciar()) {
                executor.submit(() -> {
                    tarefa.realizar();
                    System.out.println("tarefa " + tarefa.id + " feita");
                    listaTarefas.forEach(t -> Tarefa.removerDependencia(t, tarefa));
                });
            } else {
                fila.add(tarefa);
            }
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    static class Tarefa {
        int id;
        int tempo;
        List<Integer> dependencias;

        Tarefa(int id, int tempo, List<Integer> dependencias) {
            this.id = id;
            this.tempo = tempo;
            this.dependencias = dependencias;
        }

        void realizar() {
            try {
                Thread.sleep(tempo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        boolean podeIniciar() {
            return dependencias.isEmpty();
        }

        static void removerDependencia(Tarefa tarefa, Tarefa tarefaConcluida) {
            tarefa.dependencias.remove(Integer.valueOf(tarefaConcluida.id));
        }
    }
}
