# FocusFlow — Desenvolvimento do Front-end

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
