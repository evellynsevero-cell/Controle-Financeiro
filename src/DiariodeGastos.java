import java.time.LocalDate;
import java.util.*;
import java.io.*;

public class DiariodeGastos {
    private List<Gasto> gastos = new ArrayList<>();
    private static final String ARQUIVO = "gastos.csv";

    public DiariodeGastos() {
        carregarGastos();
    }

    // Adiciona um novo gasto
    public void adicionarGasto(Gasto gasto) {
        gastos.add(gasto);
        salvarGastos();
    }

    // Remove um gasto pelo índice
    public boolean removerGastos(int indice) {
        if (indice >= 0 && indice < gastos.size()) {
            gastos.remove(indice);
            salvarGastos();
            return true;
        }
        return false;
    }

    // Edita um gasto pelo índice
    public boolean editarGasto(int indice, Gasto novoGasto) {
        if (indice >= 0 && indice < gastos.size()) {
            gastos.set(indice, novoGasto);
            salvarGastos();
            return true;
        }
        return false;
    }

    // Retorna a lista de gastos
    public List<Gasto> getGastos() {
        return gastos;
    }

    // Consulta por mês (AAAA-MM)
    public List<Gasto> consultarPorMes(String anoMes) {
        List<Gasto> resultado = new ArrayList<>();
        for (Gasto g : gastos) {
            // g.getData() já é String no formato "AAAA-MM-DD"
            if (g.getData().toString().startsWith(anoMes)) { // Exemplo: "2024-06"
                resultado.add(g);
            }
        }
        return resultado;
    }

    // Consulta por semana (data de referência)
    public List<Gasto> consultarPorSemana(String dataRef) {
        List<Gasto> resultado = new ArrayList<>();
        LocalDate ref;
        try {
            ref = LocalDate.parse(dataRef); // dataRef no formato AAAA-MM-DD
        } catch (Exception e) {
            return resultado;
        }
        LocalDate inicioSemana = ref.minusDays(ref.getDayOfWeek().getValue() - 1);
        LocalDate fimSemana = inicioSemana.plusDays(6);
        for (Gasto g : gastos) {
            try {
                LocalDate dataGasto = LocalDate.parse(g.getData());
                if (!dataGasto.isBefore(inicioSemana) && !dataGasto.isAfter(fimSemana)) {
                    resultado.add(g);
                }
            } catch (Exception ignored) {
            }
        }
        return resultado;
    }

    void salvarGastos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO))) {
            pw.println("descricao,categoria,valor,data");
            for (Gasto g : gastos) {
                pw.printf(Locale.US,"%s,%s,%.2f,%s\n", g.getDescricao().trim().toLowerCase(), g.getCategoria().trim().toLowerCase(), g.getValor(), g.getData());
            }
            System.out.println("Gastos salvos em " + ARQUIVO);
        } catch (IOException e) {
            System.out.println("Erro ao salvar gastos.");
        }
    }

    // Carrega os gastos do arquivo csv
    private void carregarGastos() {
        gastos.clear();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha = br.readLine(); // cabeçalho
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                String[] partes = linha.split(",");
                if (partes.length == 4) {
                    try {
                        double valor = Double.parseDouble(partes[2]);
                        gastos.add(new Gasto(partes[0], partes[1], valor, partes[3]));
                        count++;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (count > 0) {
                System.out.println("Gastos carregados de " + ARQUIVO + ": " + count);
            }
        } catch (IOException e) {
            System.out.println("Arquivo de gastos não encontrado.");
            // Arquivo não existe ainda.
        }
    }
}
