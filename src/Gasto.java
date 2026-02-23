import java.io.Serializable;
import java.time.LocalDate;

public class Gasto implements Serializable {
    private final String descricao;
    private final String categoria;
    private final double valor;
    // formato: AAAA-MM-DD
    private CharSequence data;

    public Gasto(String descricao, String categoria, double valor, String data) {
        this.descricao =descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.data = (data.toString());
    }

    public String getDescricao() {return descricao; }
    public String getCategoria() { return categoria; }
    public double getValor() { return valor; }
    public CharSequence getData() { return data; }



    @Override
    public String toString() {
        return descricao+ " | " + categoria + " | " + valor + " | " + data;
    }



}