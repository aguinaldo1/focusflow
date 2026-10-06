# FocusFlow — Refatoração

Esta documentação registra a fase de reorganização interna do FocusFlow.

O objetivo desta etapa é reduzir responsabilidades do `FocusFlowApp` sem alterar funcionalidades já validadas.

---

# BLOCO 19K.1 — Extração do ProgressDonut

**Status:** ✅ Concluído

## Objetivo

Separar o indicador circular de progresso da classe principal da aplicação.

## Alterações

Foi criado o componente:

`ui/ProgressDonut.java`

Ele passou a ser responsável por:

- construção da rosca;
- exibição do percentual;
- cálculo visual do progresso;
- normalização do valor entre 0% e 100%.

O `FocusFlowApp` agora apenas cria o componente e informa o percentual atual.

## Validação

- 78 testes automatizados passando;
- `git diff --check` sem erros;
- formato circular preservado;
- percentual correto;
- timer e controles mantidos;
- ciclo de vida dos objetivos preservado;
- Histórico funcionando normalmente;
- nenhuma alteração visual inesperada.

## Resultado

A primeira responsabilidade visual foi removida do `FocusFlowApp` sem alteração de comportamento.
