package dss2526.service.gestao;

import dss2526.domain.entity.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface para o módulo de Gestão.
 * Define apenas os métodos necessários para o módulo de gestão.
 *
 * NOTA: Não herda de IBaseFacade para respeitar o Interface Segregation Principle.
 */
public interface IGestaoFacade {

    // ============ AUTENTICAÇÃO ============
    /**
     * Autentica um funcionário (apenas GERENTE, COO ou SYSADMIN).
     * @param user Nome de utilizador
     * @param pass Password
     * @return Funcionário autenticado ou null se inválido
     */
    Funcionario autenticarFuncionario(String user, String pass);

    // ============ GESTÃO DE FUNCIONÁRIOS ============
    /**
     * Contrata um novo funcionário num restaurante.
     * @param restauranteId ID do restaurante
     * @param novo Funcionário a contratar
     */
    void contratarFuncionario(int restauranteId, Funcionario novo);

    /**
     * Remove um funcionário (demite).
     * @param funcionarioId ID do funcionário
     */
    void demitirFuncionario(int funcionarioId);

    /**
     * Lista todos os funcionários de um restaurante.
     * @param rId ID do restaurante
     * @return Lista de funcionários
     */
    List<Funcionario> listarFuncionariosPorRestaurante(int rId);

    // ============ GESTÃO DE ESTAÇÕES ============
    /**
     * Adiciona uma estação de trabalho.
     * @param e Estação a adicionar
     */
    void adicionarEstacaoTrabalho(Estacao e);

    /**
     * Remove uma estação de trabalho.
     * @param estacaoId ID da estação
     */
    void removerEstacaoTrabalho(int estacaoId);

    /**
     * Lista todas as estações de um restaurante.
     * @param rId ID do restaurante
     * @return Lista de estações
     */
    List<Estacao> listarEstacoesPorRestaurante(int rId);

    // ============ GESTÃO DE STOCK ============
    /**
     * Atualiza o stock de um ingrediente num restaurante.
     * @param restauranteId ID do restaurante
     * @param ingredienteId ID do ingrediente
     * @param delta Quantidade a adicionar (negativo para remover)
     */
    void atualizarStockIngrediente(int restauranteId, int ingredienteId, int delta);

    // ============ LISTAGENS GERAIS ============
    /**
     * Lista todos os restaurantes.
     * @return Lista de restaurantes
     */
    List<Restaurante> listarRestaurantes();

    /**
     * Lista todos os ingredientes do sistema.
     * @return Lista de ingredientes
     */
    List<Ingrediente> listarIngredientes();

    // ============ RELATÓRIOS E ESTATÍSTICAS ============
    /**
     * Obtém um dashboard com estatísticas de um restaurante.
     * @param restauranteId ID do restaurante
     * @param inicio Data inicial (null para sem limite)
     * @param fim Data final (null para sem limite)
     * @return String formatada com as estatísticas
     */
    String obterDashboardEstatisticas(int restauranteId, LocalDateTime inicio, LocalDateTime fim);

    // ============ COMUNICAÇÃO ============
    /**
     * Envia uma mensagem local a um restaurante.
     * @param restauranteId ID do restaurante
     * @param texto Conteúdo da mensagem
     * @param nomeAutor Nome de quem envia
     */
    void enviarMensagemRestaurante(int restauranteId, String texto, String nomeAutor);

    /**
     * Difunde uma mensagem para TODOS os restaurantes.
     * @param texto Conteúdo da mensagem
     * @param nomeAutor Nome de quem envia
     */
    void difundirMensagemGlobal(String texto, String nomeAutor);
}