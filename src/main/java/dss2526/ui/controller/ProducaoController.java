package dss2526.ui.controller;

import dss2526.domain.entity.Tarefa;
import dss2526.domain.entity.Produto;
import dss2526.domain.entity.Pedido;
import dss2526.domain.enumeration.EstacaoTrabalho;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador para o KDS (Kitchen Display System).
 */
public class ProducaoController {

    public List<Tarefa> listarTarefasPorEstacao(EstacaoTrabalho e) {
        // Retorna uma tarefa dummy para testar o card visual
        List<Tarefa> lista = new ArrayList<>();
        
        Tarefa t = new Tarefa();
        t.setId(99);
        
        Produto p = new Produto(); 
        p.setNome("Cheeseburger Double");
        t.setProduto(p);
        
        Pedido ped = new Pedido(); 
        ped.setId(500);
        t.setPedido(ped);
        
        lista.add(t);
        return lista;
    }

    public void concluirTarefa(int id) {
        System.out.println("Tarefa " + id + " concluída (Mock).");
    }
}