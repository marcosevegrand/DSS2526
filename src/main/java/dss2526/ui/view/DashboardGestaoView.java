package pt.uminho.dss.restaurante.ui.view;

import pt.uminho.dss.restaurante.ui.controller.EstatisticaController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Painel simples para visualizar estatísticas.
 * Datas no formato ISO (YYYY-MM-DD).
 */
public class DashboardGestaoView extends JPanel {

    private final EstatisticaController controller;
    private final JTextField txtInicio = new JTextField(10);
    private final JTextField txtFim = new JTextField(10);
    private final JTextArea output = new JTextArea(18, 60);
    private final JSpinner spinnerTop = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

    public DashboardGestaoView(EstatisticaController controller) {
        this.controller = controller;
        inicializarUI();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Data Início (YYYY-MM-DD):"));
        txtInicio.setText(LocalDate.now().minusDays(7).toString());
        top.add(txtInicio);
        top.add(new JLabel("Data Fim:"));
        txtFim.setText(LocalDate.now().toString());
        top.add(txtFim);
        top.add(new JLabel("Top N:"));
        top.add(spinnerTop);

        JButton btnFaturacao = new JButton("Calcular Faturação");
        JButton btnTicket = new JButton("Ticket Médio");
        JButton btnTaxa = new JButton("Taxa Cancelamento");
        JButton btnTop = new JButton("Top Produtos");
        JButton btnPorDia = new JButton("Faturação por Dia");

        btnFaturacao.addActionListener(e -> calcularFaturacao());
        btnTicket.addActionListener(e -> calcularTicket());
        btnTaxa.addActionListener(e -> calcularTaxa());
        btnTop.addActionListener(e -> calcularTop());
        btnPorDia.addActionListener(e -> calcularPorDia());

        top.add(btnFaturacao);
        top.add(btnTicket);
        top.add(btnTaxa);
        top.add(btnTop);
        top.add(btnPorDia);

        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private LocalDate[] lerDatas() {
        try {
            LocalDate inicio = LocalDate.parse(txtInicio.getText().trim());
            LocalDate fim = LocalDate.parse(txtFim.getText().trim());
            return new LocalDate[]{inicio, fim};
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use YYYY-MM-DD.");
            return null;
        }
    }

    private void calcularFaturacao() {
        LocalDate[] d = lerDatas(); if (d==null) return;
        float f = controller.faturacaoTotal(d[0], d[1]);
        output.append(String.format("Faturação %s -> %s : €%.2f\n", d[0], d[1], f));
    }

    private void calcularTicket() {
        LocalDate[] d = lerDatas(); if (d==null) return;
        float f = controller.ticketMedio(d[0], d[1]);
        output.append(String.format("Ticket médio %s -> %s : €%.2f\n", d[0], d[1], f));
    }

    private void calcularTaxa() {
        LocalDate[] d = lerDatas(); if (d==null) return;
        float t = controller.taxaCancelamento(d[0], d[1]);
        output.append(String.format("Taxa de cancelamento %s -> %s : %.2f%%\n", d[0], d[1], t));
    }

    private void calcularTop() {
        LocalDate[] d = lerDatas(); if (d==null) return;
        int top = (Integer) spinnerTop.getValue();
        Map<Integer,Integer> mapa = controller.topProdutos(d[0], d[1], top);
        output.append("Top " + top + " produtos (itemId -> quantidade):\n");
        mapa.forEach((id, qtd) -> output.append(String.format("  %d -> %d\n", id, qtd)));
    }

    private void calcularPorDia() {
        LocalDate[] d = lerDatas(); if (d==null) return;
        Map<LocalDate, Float> mapa = controller.faturacaoPorDia(d[0], d[1]);
        output.append("Faturação por dia:\n");
        mapa.forEach((dia, val) -> output.append(String.format("  %s : €%.2f\n", dia, val)));
    }
}