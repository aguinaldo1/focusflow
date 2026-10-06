/# FocusFlow — Desenvolvimento do Front-end

## Objetivo

Este documento registra as decisões, implementações, testes e aprendizados relacionados à interface desktop do FocusFlow.

A lógica principal do Pomodoro já foi construída e validada anteriormente.


A partir desta etapa, o objetivo é transformar o motor existente em uma aplicação desktop visual utilizando JavaFX.

---

# Estado inicial

Antes do início do front-end, o FocusFlow já possui:

- configuração do Pomodoro;
- fases de foco e pausas;
- controle de estado;
- contagem regressiva;
- relógio real;
- pause e resume;
- reset;
- snapshot de estado;
- testes automatizados;
- interface de demonstração via terminal.

Componentes existentes:

```text
PomodoroConfig
PomodoroPhase
PomodoroStatus
PomodoroSession
PomodoroTimer
PomodoroClock
PomodoroSnapshot

```

---

# Arquitetura

A interface JavaFX consumirá o motor existente sem reimplementar as regras do Pomodoro.

```text
JavaFX
  │
  ▼
PomodoroTimer
  │
  ▼
PomodoroSession

PomodoroClock
  │
  ▼
PomodoroTimer
```

## Princípio

```text
Domínio  → regras
Timer    → tempo restante
Clock    → passagem do tempo
JavaFX   → apresentação e interação
```

---

# Roadmap do Front-end

## BLOCO 10 — Fundação JavaFX
- adicionar JavaFX;
- abrir a primeira janela;
- validar execução.

## BLOCO 11 — Estado visual
- fase;
- status;
- tempo restante;
- `PomodoroSnapshot`.

## BLOCO 12 — Controles
- iniciar;
- pausar;
- continuar;
- resetar.

## BLOCO 13 — Atualização em tempo real
- atualizar o cronômetro;
- tratar a JavaFX Application Thread.

## BLOCO 14 — Widget desktop
- layout compacto;
- always-on-top;
- refinamentos de UX.

---

# BLOCO 10 — Fundação JavaFX

**Status:** ⏳ Não iniciado

## Objetivo

Introduzir JavaFX sem alterar o motor do Pomodoro já validado.

A primeira entrega será uma janela mínima executável.

---

# BLOCO 10 — Fundação JavaFX

**Status:** ✅ Concluído

## Implementação

Foi adicionado JavaFX ao projeto Maven e criada a primeira aplicação gráfica:

```text
FocusFlowApp

---

# BLOCO 11 — Exibição do estado do Pomodoro

**Status:** ✅ Concluído

## Implementação

A interface JavaFX passou a consumir um `PomodoroSnapshot` real do motor.

Foram exibidos:

```text
FOCUS
25:00
IDLE
Ciclos concluídos: 0

---

# BLOCO 12 — Controles visuais

**Status:** ✅ Concluído

## Implementação

Foram adicionados controles visuais para:

- iniciar;
- pausar;
- continuar;
- resetar.

Os botões executam comandos diretamente no `PomodoroTimer` e atualizam a interface utilizando um novo `PomodoroSnapshot`.

Os botões também são habilitados ou desabilitados conforme o estado atual:

```text
IDLE    → Iniciar
RUNNING → Pausar
PAUSED  → Continuar

---

# BLOCO 13 — Atualização em tempo real

**Status:** ✅ Concluído

## Implementação

O `PomodoroClock` foi conectado à interface JavaFX.

A interface utiliza um `Timeline` apenas para consultar periodicamente o `PomodoroSnapshot` e atualizar a tela.

Responsabilidades:

```text
PomodoroClock → passagem real do tempo
PomodoroTimer → estado e tempo restante
Timeline      → atualização visual

---

# BLOCO 14 — Widget desktop compacto

**Status:** ✅ Concluído

## Implementação

A interface foi ajustada para funcionar como um widget desktop compacto.

Principais mudanças:

- janela reduzida para `390x230`;
- janela não redimensionável;
- `always-on-top`;
- cronômetro com maior destaque visual;
- espaçamentos reduzidos;
- controles preservados.

## Validação

Foram validados manualmente:

```text
Iniciar
Pausar
Continuar
Resetar
Cronômetro em tempo real
Always-on-top

---

# BLOCO 15 — Atalhos de teclado

**Status:** ✅ Concluído

## Implementação

Foram adicionados atalhos locais da aplicação utilizando accelerators do JavaFX:

```text
Ctrl + I → Iniciar
Ctrl + P → Pausar
Ctrl + C → Continuar
Ctrl + R → Resetar

---

# BLOCO 16A — Capacidades do ambiente desktop

**Status:** ✅ Concluído

## Implementação

Foi criada uma camada para detectar capacidades específicas do ambiente desktop.

O FocusFlow agora consegue identificar:

- disponibilidade de ambiente gráfico;
- disponibilidade de `SystemTray`.

No ambiente atual WSL2/WSLg:

```text
Headless: false
SystemTray supported: false

### Refinamento WSL

A detecção de `SystemTray` passou a reconhecer previamente ambientes WSL.

Quando executado no WSL, o FocusFlow retorna `SystemTray = false` sem realizar a consulta nativa ao AWT, evitando inicialização desnecessária e mantendo os testes independentes da plataforma.

Validação:

```text
WSL_DISTRO_NAME=Ubuntu-24.04
Tests run: 27
Failures: 0
Errors: 0

---

# BLOCO 16B — Integração opcional com System Tray

**Status:** ✅ Implementado / ⏳ validação nativa Windows pendente

## Implementação

Foi criada a classe `SystemTrayIntegration`, responsável pela integração opcional com a bandeja do sistema.

Quando o ambiente oferece `SystemTray`, o FocusFlow poderá:

- ocultar a janela ao fechar;
- permanecer executando em segundo plano;
- restaurar a janela pelo ícone do tray;
- abrir pelo menu `Abrir FocusFlow`;
- encerrar pelo menu `Sair`.

A comunicação entre AWT e JavaFX utiliza `Platform.runLater()` para preservar a separação entre as threads das duas tecnologias.

## Validação no WSL

No WSL2/WSLg o System Tray não está disponível.

Mesmo assim, foram validados:

- inicialização normal do FocusFlow;
- funcionamento do Pomodoro;
- fechamento normal da aplicação;
- fallback sem System Tray;
- 27 testes permanecem passando.

```text
Tests run: 27
Failures: 0
Errors: 0
BUILD SUCCESS

---

# BLOCO 16C — Alerta sonoro do timer

**Status:** ✅ Concluído

## Implementação

O `PomodoroTimer` passou a informar quando um intervalo termina.

O `PomodoroClock` utiliza esse evento para disparar uma ação de conclusão, atualmente conectada ao `SystemAlert`.

Fluxo:

```text
PomodoroTimer
    ↓
intervalo concluído
    ↓
PomodoroClock
    ↓
SystemAlert
    ↓
alerta sonoro

---

# BLOCO 17A — Modelo de objetivos

**Status:** ✅ Concluído

## Implementação

Foi criado o modelo `Objective`, que representa um objetivo personalizado do usuário.

Cada objetivo possui:

- identificador único;
- nome obrigatório;
- descrição opcional;
- data de criação;
- status;
- data de encerramento;
- `PomodoroTimer` independente.

Estados disponíveis:

```text
ACTIVE
COMPLETED
NOT_FINISHED
DISCARDED

---

# BLOCO 17B — Gerenciamento de objetivos

**Status:** ✅ Concluído

## Implementação

Foi criado o `ObjectiveManager`, responsável por coordenar os objetivos do FocusFlow.

Principais regras:

- máximo de 5 objetivos ativos;
- primeiro objetivo criado é selecionado automaticamente;
- cada objetivo mantém seu próprio `PomodoroTimer`;
- ao trocar de objetivo, o anterior é pausado se estiver em execução;
- o novo objetivo não inicia automaticamente;
- tempo e ciclos são preservados entre trocas;
- objetivos encerrados permanecem disponíveis para o futuro histórico;
- um objetivo encerrado deixa de ocupar uma das 5 vagas ativas.

## Validação

```text
ObjectiveManagerTest: 7 testes

Tests run: 43
Failures: 0
Errors: 0
BUILD SUCCESS

---

# BLOCO 17C — Ciclo de vida dos objetivos

**Status:** ✅ Concluído

## Implementação

O `ObjectiveManager` passou a coordenar o encerramento dos objetivos.

Operações disponíveis:

```text
completeObjective(...)
markObjectiveNotFinished(...)
discardObjective(...)

---

# BLOCO 18A — Fundação SQLite

**Status:** ✅ Concluído

## Implementação

Foi adicionada persistência local com SQLite através do driver JDBC.

Criado o `DatabaseManager`, responsável por:

- abrir conexões SQLite;
- inicializar o banco;
- criar a tabela `objectives`;
- permitir inicialização repetida sem apagar dados existentes.

A estrutura inicial armazena:

```text
id
name
description
status
created_at
closed_at
pomodoro_phase
pomodoro_status
completed_focus_cycles
remaining_seconds
