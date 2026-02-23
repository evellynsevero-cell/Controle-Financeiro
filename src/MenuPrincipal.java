import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.List;

public class MenuPrincipal {
    public static void main(String[] args) {

        DiariodeGastos diario = new DiariodeGastos();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n --- Diário de gastos ---");
            System.out.println("1. Anotar novo gasto");
            System.out.println("2. Corrigir gasto");
            System.out.println("3. Remover gastos");
            System.out.println("4. Consultar gastos");
            System.out.println("5. Sair");
            System.out.println("Escolha uma opção: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    adicionarGasto(diario, scanner);
                    break;
                case "2":
                    corrigirGasto(diario, scanner);
                    break;
                case "3":
                    removerGasto(diario, scanner);
                    break;
                case "4":
                    consultarGastos(diario, scanner);
                    break;
                case "5":
                    diario.salvarGastos();
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void adicionarGasto(DiariodeGastos diario, Scanner scanner) {
        System.out.println("Descrição: ");
        String descricao = scanner.nextLine();
        System.out.println("Categoria: ");
        String categoria = scanner.nextLine();

        double valor;
        while (true) {
            System.out.println("Valor: ");
            try {
                valor = Double.parseDouble(scanner.nextLine());
                if (valor < 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um valor válido.");
            }
        }

        LocalDate data;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (true) {
            System.out.println("Data (DD-MM-AAAA): ");
            String dataStr = scanner.nextLine();
            try {
                data = LocalDate.parse(dataStr, formatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato DD-MM-AAAA.");
            }
        }

        diario.adicionarGasto(new Gasto(descricao, categoria, valor, data.toString()));
        System.out.println("Gasto adicionado!");
    }

    private static void corrigirGasto(DiariodeGastos diario, Scanner scanner) {
        List<Gasto> lista = diario.getGastos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum gasto registrado.");
            return;
        }
        listarGastos(lista);
        int indice = escolherIndice(lista.size(), scanner, "corrigir");
        if (indice == -1) return;
        Gasto antigo = lista.get(indice);

        System.out.println("Nova descrição (" + antigo.getDescricao() + "): ");
        String desc = scanner.nextLine();
        if (desc.isEmpty()) desc = antigo.getDescricao();

        System.out.println("Nova categoria (" + antigo.getCategoria() + "): ");
        String cat = scanner.nextLine();
        if (cat.isEmpty()) cat = antigo.getCategoria();

        double valor = antigo.getValor();
        System.out.println("Novo valor (" + antigo.getValor() + "):");
        String valStr = scanner.nextLine();
        if (!valStr.isEmpty()) {
            try {
                double novoValor = Double.parseDouble(valStr);
                if (novoValor >= 0) valor = novoValor;
                else System.out.println("Valor inválido. Mantendo valor anterior.");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Mantendo valor anterior.");
            }
        }

        // Mostra a data antiga no formato dd-MM-yyyy
        LocalDate dataAntiga = LocalDate.parse(antigo.getData());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        System.out.println("Nova data (" + dataAntiga.format(formatter) + "): ");
        String datStr = scanner.nextLine();
        LocalDate novaData = dataAntiga;
        if (!datStr.isEmpty()) {
            try {
                novaData = LocalDate.parse(datStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Mantendo data anterior.");
            }
        }

        diario.editarGasto(indice, new Gasto(desc, cat, valor, novaData.toString()));
        System.out.println("Gasto corrigido!");
    }

    private static void removerGasto(DiariodeGastos diario, Scanner scanner) {
        List<Gasto> lista = diario.getGastos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum gasto registrado.");
            return;
        }
        listarGastos(lista);
        int indice = escolherIndice(lista.size(), scanner, "remover");
        if (indice == -1) return;

        if (diario.removerGastos(indice)) {
            System.out.println("Gasto removido!");
        } else {
            System.out.println("Índice inválido.");
        }
    }

    private static void consultarGastos(DiariodeGastos diario, Scanner scanner) {
        System.out.print("Consultar por (1) Semana ou (2) Mês? ");
        String op = scanner.nextLine();
        List<Gasto> resultado;

        if ("1".equals(op)) {
            System.out.print("Data de referência (AAAA-MM-DD): ");
            String dataRef = scanner.nextLine();
            try {
                resultado = diario.consultarPorSemana(dataRef);
                System.out.println("\n--- Gastos da semana ---");
                listarGastos(resultado);

            } catch (Exception e) {
                System.out.println("Data inválida.");

            }
        } else if ("2".equals(op)) {
            System.out.print( "Mes e ano (AAAA-MM): ");
            String anoMes = scanner.nextLine();
            try {
                resultado = diario.consultarPorMes(anoMes);
                System.out.println("\n--- Gastos do mês ---");
                listarGastos(resultado);

            } catch (Exception e) {
                System.out.println("Formato inválido.");

            }
        } else {
            System.out.println("Opção inválida.");

        }
    }

    private static void listarGastos(List<Gasto> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum gasto encontrado.");
            return;
        }
        int i = 1;
        for (Gasto g : lista) {
            System.out.printf("%d. %s\n", i++, g.toString());
        }
    }

    private static int escolherIndice(int tamanho, Scanner scanner, String acao) {
        System.out.print("Digite o número do gasto a " + acao + ": ");
        try {
            int num = Integer.parseInt(scanner.nextLine());
            if (num >= 1 && num <= tamanho)
                return num - 1;
            else
                System.out.println("Número inválido.");
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
        return -1;

    }
}