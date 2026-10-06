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

---

# BLOCO 19K.3 — Extração do ObjectivePanel

**Status:** ✅ Concluído

## Objetivo

Separar da classe principal a apresentação e os controles relacionados aos objetivos.

## Alterações

Foi criado:

`ui/ObjectivePanel.java`

O componente passou a concentrar:

- cadastro de objetivos;
- seleção de objetivo ativo;
- escolha de complexidade;
- planejamento de ciclos;
- aumento e redução dos ciclos planejados;
- ações de Finalizar, Não finalizado e Remover;
- estado visual com e sem objetivo selecionado;
- bloqueio de novos cadastros ao atingir o limite de objetivos ativos.

O `FocusFlowApp` continua coordenando as ações de domínio, persistência e timer, enquanto o `ObjectivePanel` cuida da apresentação dos objetivos.

## Validação

- 78 testes automatizados passando;
- cadastro e seleção funcionando;
- complexidades Fácil, Médio e Difícil preservadas;
- planejamento de ciclos funcionando;
- Finalizar, Não finalizado e Remover funcionando;
- timer e ProgressDonut preservados;
- Histórico preservado;
- nenhuma regressão visual identificada.

## Resultado

A interface de objetivos foi isolada em um componente próprio, reduzindo significativamente as responsabilidades do `FocusFlowApp`.

---

# BLOCO 19K.4 — Extração do PomodoroControls

**Status:** ✅ Concluído

## Objetivo

Separar da classe principal os controles visuais do Pomodoro.

## Alterações

Foi criado:

`ui/PomodoroControls.java`

O componente passou a ser responsável por:

- botão Iniciar;
- botão Pausar;
- botão Continuar;
- botão Reiniciar;
- estado habilitado/desabilitado dos controles conforme o status do Pomodoro.

O `FocusFlowApp` continua responsável pelas ações executadas por cada controle.

## Validação

- 78 testes automatizados passando;
- `PomodoroControls` integrado ao `FocusFlowApp`;
- Iniciar, Pausar, Continuar e Reiniciar funcionando;
- estados habilitado/desabilitado preservados;
- atalhos Ctrl+I, Ctrl+P, Ctrl+C e Ctrl+R funcionando;
- Ctrl+C continua copiando texto quando o foco está em um campo de entrada;
- timer e ProgressDonut preservados;
- Histórico preservado;
- nenhuma regressão visual identificada.

## Resultado

Os controles do Pomodoro foram isolados em um componente próprio, reduzindo mais uma responsabilidade visual do `FocusFlowApp`.

---

# BLOCO 19K.5 — Extração dos diálogos de ciclo de vida

**Status:** ✅ Concluído

## Objetivo

Remover do `FocusFlowApp` a responsabilidade de construir os diálogos de confirmação relacionados ao ciclo de vida dos objetivos.

## Alterações

Foi criado:

`ui/ObjectiveLifecycleDialogs.java`

A classe passou a concentrar as confirmações de:

- Finalizar;
- Não finalizado;
- Remover.

Também foram mantidos:

- nome do objetivo na confirmação;
- progresso atual para Finalizar e Não finalizado;
- mensagem de descarte para Remover;
- possibilidade de cancelar a operação.

O `FocusFlowApp` continua responsável por executar as ações de domínio após a confirmação do usuário.

## Validação

- 78 testes automatizados passando;
- diálogos integrados ao `FocusFlowApp`;
- Finalizar funcionando;
- Não finalizado funcionando;
- Remover funcionando;
- cancelamento preservado;
- progresso exibido nas confirmações corretas;
- Histórico preservado;
- nenhuma regressão visual identificada.

## Resultado

Os diálogos de ciclo de vida foram isolados da classe principal, reduzindo responsabilidade visual e duplicação no `FocusFlowApp`.
