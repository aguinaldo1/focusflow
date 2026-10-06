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

---

# BLOCO 19K.2 — Extração do PomodoroPanel

**Status:** ✅ Concluído

## Objetivo

Separar a apresentação visual do timer da classe principal da aplicação.

## Alterações

Foi criado:

`ui/PomodoroPanel.java`

O componente passou a ser responsável por:

- fase atual do Pomodoro;
- tempo restante;
- status do timer;
- ciclos concluídos;
- ProgressDonut;
- aparência visual de foco e pausas;
- estado visual sem objetivo selecionado.

O `FocusFlowApp` agora apenas fornece o estado atual ao painel.

## Validação

- 78 testes automatizados passando;
- `FocusFlowApp` utilizando `PomodoroPanel`;
- timer e rosca preservados;
- cores de FOCO e PAUSA preservadas;
- controles do timer funcionando;
- Histórico funcionando;
- nenhuma regressão visual identificada.

## Resultado

A responsabilidade de apresentação do Pomodoro foi removida do `FocusFlowApp`, reduzindo o acoplamento da classe principal sem alterar o comportamento do aplicativo.
