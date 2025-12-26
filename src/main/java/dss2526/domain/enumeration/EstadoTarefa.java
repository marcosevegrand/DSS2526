package dss2526.domain.enumeration;

public enum EstadoTarefa {
    PENDENTE,       // Criada, mas ainda não iniciada
    EM_EXECUCAO,    // Iniciada (dataInicio != null)
    ATRASADA,       // Parada por falta de ingrediente ou outro problema
    CONCLUIDA       // Finalizada
}