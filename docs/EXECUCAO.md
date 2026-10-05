# FocusFlow — Documento Mestre de Execução e Decisões

## 1. Propósito

O FocusFlow é um aplicativo desktop de Pomodoro inteligente e adaptativo, criado primeiro para uso real do próprio autor.

O projeto deve demonstrar mais do que capacidade de programar. Ele deve evidenciar:

1. definição de escopo;
2. tomada de decisões técnicas;
3. análise de trade-offs;
4. priorização;
5. capacidade de deixar funcionalidades de fora;
6. validação com uso real;
7. evolução incremental;
8. documentação do raciocínio.

> Regra principal: não construir complexidade por complexidade. Construir apenas o necessário para resolver um problema real e registrar as decisões tomadas.

---

## 2. Problema real

Ao estudar ou executar atividades de concentração, o usuário precisa de um Pomodoro que:

- fique visível sem atrapalhar a tela;
- possa ser acionado rapidamente;
- mantenha até 5 atividades simultâneas;
- permita alternar entre atividades sem perder progresso;
- permita recomeçar ou finalizar uma atividade;
- recomende a quantidade de ciclos conforme a dificuldade;
- mantenha histórico para ajustar futuras recomendações.

O aplicativo será utilizado no dia a dia pelo próprio autor, portanto resolve um problema real fora do repositório.

---

## 3. Hipótese de produto

Se o usuário puder registrar até cinco objetivos, iniciar um cronômetro flutuante e receber uma estimativa simples de esforço, então conseguirá organizar sessões de estudo sem depender de múltiplas ferramentas.

O MVP será considerado útil quando puder ser usado durante uma semana real de estudos.

---

## 4. Escopo do MVP

### Incluído

- Pomodoro de 25 minutos;
- pausa curta de 5 minutos;
- pausa longa após 4 ciclos;
- iniciar, pausar, continuar e reiniciar;
- contagem de ciclos concluídos;
- até 5 objetivos ativos;
- recomeçar objetivo;
- finalizar objetivo;
- adicionar novo objetivo;
- classificação de complexidade 🟢 🟡 🔴;
- estimativa de quantidade de Pomodoros;
- persistência local;
- janela flutuante;
- histórico básico;
- executável Windows.

### Fora do MVP

- login;
- sincronização em nuvem;
- aplicativo mobile;
- colaboração;
- gamificação;
- integração com calendário;
- IA generativa;
- geração automática de questões;
- dashboard avançado;
- backend remoto.

Motivo: nada disso é necessário para validar o problema principal.

---

## 5. Decisões técnicas iniciais

### Java 21

**Motivos:** aprofundar Java, forte tipagem, ecossistema maduro e possibilidade de empacotamento nativo com `jpackage`.

**Alternativas consideradas:** Python, Electron, Tauri e C#/.NET.

**Decisão:** Java 21.

### JavaFX

**Motivos:** integração natural com Java, suporte a janela desktop e `always-on-top`, e evita introduzir uma stack web no MVP.

**Trade-off:** Electron teria ecossistema visual maior, mas acrescentaria JavaScript/Node e maior consumo de recursos.

**Decisão:** JavaFX.

### Maven

**Motivos:** já disponível no ambiente, simples e suficiente para dependências e testes.

**Decisão:** Maven.

### SQLite

**Motivos:** banco local, arquivo único e sem servidor.

**Trade-off:** JSON seria mais simples no início, mas SQLite facilita consultas e evolução futura.

**Decisão:** SQLite somente quando a persistência realmente entrar no projeto.

### Estratégia de desenvolvimento

Separar o motor de Pomodoro da interface gráfica.

Primeiro construir e testar a lógica. Depois construir JavaFX.

**Motivo:** reduzir acoplamento e tornar as regras testáveis sem depender da interface.

---

## 6. Ambiente de desenvolvimento

Ambiente inicial:

- Ubuntu 24.04 em WSL2;
- Java 21;
- Maven;
- Git;
- VS Code.

Estratégia:

- lógica do domínio inicialmente no WSL;
- JavaFX poderá ser validado com WSLg;
- empacotamento `.exe` posteriormente em Windows nativo.

**Motivo:** `jpackage` gera pacote nativo da plataforma em que é executado.

---

## 7. Estratégia Git

Cada commit deve representar um incremento compreensível.

Formato:

```text
tipo: descrição objetiva
```

Tipos preferidos:

- `chore:` estrutura/configuração;
- `docs:` documentação;
- `feat:` funcionalidade;
- `test:` teste;
- `refactor:` melhoria interna;
- `fix:` correção.

Exemplo:

```text
feat: implementa estados básicos do pomodoro
```

---

## 8. Roadmap controlado

### Fase 0 — Fundação

Entregas:

- estrutura inicial;
- README;
- documento de execução;
- `.gitignore`;
- Maven configurado;
- primeiro commit.

Critério de conclusão:

```bash
mvn test
```

executa com sucesso.

### Fase 1 — Motor de Pomodoro

Construir sem GUI.

Modelagem definida:

**Fases do Pomodoro:**

```text
FOCUS
SHORT_BREAK
LONG_BREAK
```

**Status de execução:**

```text
IDLE
RUNNING
PAUSED
```

A fase e o status são modelados separadamente para preservar o contexto do intervalo quando a sessão estiver pausada.

Entregas:

- iniciar sessão;
- pausar;
- continuar;
- reiniciar;
- concluir foco;
- concluir pausa;
- contador de Pomodoros.

### Fase 2 — Tempo real

Adicionar contagem regressiva de 25, 5 e pausa longa. O relógio deverá ser testável sem esperar 25 minutos nos testes automatizados.

### Fase 3 — Objetivos

Criar `StudyGoal` com campos iniciais:

```text
id
title
complexity
plannedPomodoros
completedPomodoros
status
```

Regra: máximo de 5 objetivos ativos.

### Fase 4 — Motor de complexidade

Critérios:

- dificuldade conceitual;
- quantidade de operações;
- dificuldade de implementação;
- importância;
- pré-requisitos;
- performance anterior.

Pontuação inicial:

```text
0–4  → 🟢 baixa
5–8  → 🟡 média
9–12 → 🔴 alta
```

### Fase 5 — Persistência

Adicionar SQLite quando as entidades estiverem estáveis.

### Fase 6 — JavaFX

Criar primeira interface funcional, sem preocupação com estética final.

### Fase 7 — Janela flutuante

Adicionar `always-on-top`, modo compacto e arraste.

### Fase 8 — Gestão visual de objetivos

Listar até 5 objetivos, selecionar, recomeçar, finalizar e adicionar novo.

### Fase 9 — Histórico

Registrar sessões concluídas, minutos estudados, Pomodoros realizados e performance.

### Fase 10 — Integração Windows

Ícone, system tray, atalhos e instalador.

### Fase 11 — Uso real

Usar por pelo menos uma semana e registrar:

- o que incomodou;
- o que foi desnecessário;
- o que faltou;
- bugs reais;
- mudanças feitas após uso.

---

## 9. Modelo de registro de decisão

```text
## DEC-XXX — Nome da decisão

Data:
Contexto:
Opções consideradas:
Decisão:
Motivo:
Trade-offs:
Consequências:
```

---

## 10. Modelo de registro de execução

```text
## BLOCO XX — Nome

Objetivo:
Antes de executar:
Comandos executados:
Resultado:
Problemas encontrados:
Decisões tomadas:
Evidência:
Commit:
Próximo passo:
```

---

## 11. Perguntas que o projeto deve responder em entrevista

- Por que esse problema foi escolhido?
- Por que Java 21?
- Por que JavaFX?
- Por que o motor foi separado da interface?
- Por que SQLite em vez de um backend remoto?
- O que foi deliberadamente deixado de fora?
- Quais trade-offs apareceram durante a execução?
- O que mudou depois do uso real?
- Quais bugs reais apareceram?
- Como as decisões foram verificadas?

---

## 12. Métricas de validação

- sessões reais executadas;
- dias em que o app foi usado;
- objetivos concluídos;
- interrupções/bugs;
- diferença entre Pomodoros estimados e realizados;
- funcionalidades removidas após uso real.

---

# 13. Primeira execução — Fase 0

Nesta primeira sessão **não implementar o cronômetro**.

Objetivo:

> Criar uma base reproduzível, documentada e versionada.

## Passo 1 — criar diretório

```bash
mkdir -p ~/projetos/focusflow
cd ~/projetos/focusflow
```

## Passo 2 — iniciar Git

```bash
git init
git branch -M main
```

## Passo 3 — confirmar ambiente

```bash
java -version
mvn -version
git --version
pwd
```

Registrar a saída neste documento.

## Passo 4 — criar estrutura inicial

```bash
mkdir -p docs
mkdir -p src/main/java/io/github/aguinaldo1/focusflow
mkdir -p src/test/java/io/github/aguinaldo1/focusflow

touch README.md
touch docs/EXECUCAO.md
touch .gitignore
touch pom.xml
```

Estrutura esperada:

```text
focusflow/
├── .gitignore
├── README.md
├── pom.xml
├── docs/
│   └── EXECUCAO.md
└── src/
    ├── main/java/io/github/aguinaldo1/focusflow/
    └── test/java/io/github/aguinaldo1/focusflow/
```

## Passo 5 — configurar o Maven mínimo

O `pom.xml` deve começar simples. Dependências de JavaFX, SQLite e outras bibliotecas serão adicionadas somente quando forem necessárias.

Conteúdo inicial:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>io.github.aguinaldo1</groupId>
    <artifactId>focusflow</artifactId>
    <version>0.1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

Conteúdo inicial de `.gitignore`:

```gitignore
target/
.idea/
.vscode/
*.iml
*.log
.DS_Store
```

## Passo 6 — validar a fundação

```bash
mvn test
```

Resultado esperado:

```text
BUILD SUCCESS
```

Esse resultado será a primeira evidência técnica do projeto.

---

## 14. Primeiro checkpoint Git

Somente depois de `mvn test` funcionar:

```bash
git status
git add .
git commit -m "chore: inicia estrutura do projeto FocusFlow"
```

A Fase 0 termina quando:

- a estrutura existe;
- o Maven reconhece o projeto;
- `mvn test` retorna `BUILD SUCCESS`;
- o primeiro commit foi criado.

Somente depois disso começa a Fase 1.

---

## 15. Regra de trabalho daqui em diante

```text
explicar
↓
executar
↓
verificar
↓
documentar
↓
commit
↓
push
↓
próxima etapa
```

O autor executará os comandos em sua máquina e enviará a saída relevante antes de avançarmos.

Isso preserva aprendizado, controle e evidências reais do desenvolvimento.


---

## 16. Diário real de execução

### BLOCO 01 — Validação do ambiente

**Data:** 05/10/2026

**Objetivo**

Confirmar que o ambiente necessário para iniciar o FocusFlow está funcional.

**Evidências coletadas**

#### Java

```text
openjdk 21.0.12.1 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-24.04.4-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-24.04.4-Ubuntu, mixed mode, sharing)
```

#### Maven

```text
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 21.0.12.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.18.33.2-microsoft-standard-wsl2", arch: "amd64", family: "unix"
```

#### Git

```text
git version 2.43.0
```

#### Sistema

```text
Ubuntu 24.04 em WSL2
Kernel: 6.18.33.2-microsoft-standard-wsl2
Arquitetura: amd64
```

#### Diretório do projeto

```text
/home/aguin/projetos/focusflow
```

**Resultado**

✅ Ambiente aprovado para iniciar o desenvolvimento.

- Java 21 está instalado e ativo.
- Maven está executando sobre o Java 21.
- Git está disponível.
- O projeto está em um diretório dedicado dentro do WSL2.
- Não foi identificado bloqueio que justifique atualização de ferramenta antes de começar.

**Decisão**

Manter as versões atuais durante a fundação do projeto. Atualizações de Maven, Java ou Git só serão feitas se surgir uma necessidade concreta durante a implementação.

**Motivo**

Atualizar ferramentas sem necessidade adicionaria mudança e risco sem gerar valor para o MVP.

**Próximo passo**

BLOCO 02 — criar a estrutura Maven mínima, validar o `pom.xml` e executar o primeiro `mvn test`.

---

# BLOCO 02 — Fundação Maven

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Criar uma estrutura mínima e reproduzível para o FocusFlow antes da implementação das regras de negócio.

## Ambiente validado

- Java: OpenJDK 21.0.12.1
- Maven: Apache Maven 3.8.7
- Git: 2.43.0
- Sistema: Ubuntu 24.04 / WSL2
- Kernel: 6.18.33.2-microsoft-standard-wsl2
- Diretório do projeto: `/home/aguin/projetos/focusflow`

## Estrutura inicial

```text
focusflow/
├── .gitignore
├── README.md
├── pom.xml
├── docs/
│   └── EXECUCAO.md
└── src/
    ├── main/java/io/github/aguinaldo1/focusflow/
    └── test/java/io/github/aguinaldo1/focusflow/
```

## Decisões tomadas

### Maven como ferramenta de build

Maven foi mantido como ferramenta de build por já estar disponível no ambiente e ser suficiente para compilação, testes e gerenciamento de dependências.

### Java 21

Foi mantido o Java 21 já instalado no ambiente.

Não houve atualização de versão apenas por existir uma versão mais recente.

**Motivo:** evitar mudança e risco sem necessidade concreta para o MVP.

### Dependências mínimas

JavaFX, SQLite e frameworks adicionais não foram adicionados durante a fundação.

A decisão foi introduzir dependências somente quando uma funcionalidade real passar a exigir seu uso.

### Diretórios vazios

Os diretórios de código foram criados antes das primeiras classes Java.

Como Git não versiona diretórios vazios, foi decidido não criar arquivos `.gitkeep` apenas para forçar sua presença no repositório.

### Arquivos de build

O diretório Maven:

```text
target/
```

foi incluído no `.gitignore`.

Validação:

```text
!! target/
```

Isso confirmou que os artefatos locais de build não seriam enviados ao GitHub.

## Validação Maven

Comando executado:

```bash
mvn test
```

Resultado:

```text
[INFO] No tests to run.
[INFO] BUILD SUCCESS
[INFO] Total time: 19.242 s
```

Neste estágio ainda não existiam testes automatizados, portanto `No tests to run` era o comportamento esperado.

A execução validou:

- leitura do `pom.xml`;
- compatibilidade com Java 21;
- funcionamento do Maven;
- ciclo básico de build.

## Primeiro commit

```text
3e11ac6 chore: inicia estrutura do projeto FocusFlow
```

Após o commit:

```text
On branch main
nothing to commit, working tree clean
```

## Commit de documentação

Após registrar a validação da fundação:

```text
3ccc6bc docs: registra validação da fundação Maven
```

## Conclusão

A fundação técnica do FocusFlow ficou funcional, reproduzível e versionada.

O projeto estava pronto para ser publicado em um repositório remoto antes do início das regras de domínio.

---

# BLOCO 03 — Publicação no GitHub

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Publicar a fundação técnica do FocusFlow em um repositório remoto, preservando o histórico Git criado localmente.

## Repositório remoto

```text
git@github.com:aguinaldo1/focusflow.git
```

## Estratégia de autenticação

Foi utilizado SSH para comunicação com o GitHub.

A autenticação SSH já estava funcional, portanto não houve motivo para migrar para HTTPS.

Validação:

```text
origin  git@github.com:aguinaldo1/focusflow.git (fetch)
origin  git@github.com:aguinaldo1/focusflow.git (push)
```

## Primeiro push

Comando executado:

```bash
git push -u origin main
```

Resultado:

```text
[new branch] main -> main
branch 'main' set up to track 'origin/main'
```

A opção `-u` configurou a branch local `main` para rastrear `origin/main`.

## Validação do rastreamento

Comando:

```bash
git branch -vv
```

Resultado:

```text
* main 3ccc6bc [origin/main] docs: registra validação da fundação Maven
```

Estado final após a publicação:

```text
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean
```

## Commit de documentação

Após registrar a publicação inicial:

```text
335105d docs: registra publicação inicial no GitHub
```

## Conclusão

O repositório local e o repositório remoto passaram a estar sincronizados.

A partir deste ponto, cada incremento validado do FocusFlow pode seguir o fluxo:

```text
implementar
↓
testar
↓
documentar
↓
commit
↓
push
```

---

# BLOCO 04 — Modelagem inicial do motor Pomodoro

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Criar o primeiro domínio funcional do FocusFlow antes da implementação do cronômetro real e da interface gráfica.

O foco deste bloco foi representar corretamente as regras e transições de uma sessão Pomodoro.

## Problema de modelagem

Uma primeira possibilidade seria representar tudo em um único estado:

```text
IDLE
FOCUS
SHORT_BREAK
LONG_BREAK
PAUSED
```

Essa abordagem apresenta uma ambiguidade.

Ao entrar em `PAUSED`, o sistema deixaria de expressar diretamente qual intervalo estava pausado:

- foco;
- pausa curta;
- pausa longa.

## DEC-001 — Separar fase e status de execução

**Data:** 05/10/2026

**Contexto:** era necessário representar tanto o tipo do intervalo quanto a condição atual da execução.

**Opções consideradas:**

1. um único enum contendo todas as combinações;
2. um estado `PAUSED` com armazenamento adicional do estado anterior;
3. separar fase e status de execução.

**Decisão:** separar fase e status.

### Fases

```text
FOCUS
SHORT_BREAK
LONG_BREAK
```

### Status

```text
IDLE
RUNNING
PAUSED
```

Isso permite representar situações como:

```text
phase  = FOCUS
status = PAUSED
```

ou:

```text
phase  = SHORT_BREAK
status = RUNNING
```

**Benefício:** o sistema preserva o contexto sem precisar criar estados artificiais como:

```text
PAUSED_FOCUS
PAUSED_SHORT_BREAK
PAUSED_LONG_BREAK
```

**Trade-off:** a sessão passa a manter duas propriedades relacionadas em vez de apenas uma.

## Dependência de testes

O projeto passou a utilizar JUnit 5 para testes automatizados.

Versão configurada:

```text
JUnit 5.11.4
```

Também foi configurado o Maven Surefire Plugin para execução dos testes.

## Estrutura criada

```text
src/main/java/io/github/aguinaldo1/focusflow/pomodoro/
├── PomodoroPhase.java
├── PomodoroSession.java
└── PomodoroStatus.java

src/test/java/io/github/aguinaldo1/focusflow/pomodoro/
└── PomodoroSessionTest.java
```

## Responsabilidades

### `PomodoroPhase`

Representa o tipo do intervalo atual:

```text
FOCUS
SHORT_BREAK
LONG_BREAK
```

### `PomodoroStatus`

Representa a condição de execução:

```text
IDLE
RUNNING
PAUSED
```

### `PomodoroSession`

Responsável pelas regras de transição da sessão.

Comportamentos iniciais:

- iniciar;
- pausar;
- continuar;
- concluir intervalo;
- reiniciar;
- contabilizar ciclos de foco concluídos.

## Regras implementadas

A sessão:

- nasce em `FOCUS`;
- nasce com status `IDLE`;
- começa com zero ciclos de foco concluídos;
- pode iniciar apenas quando estiver `IDLE`;
- pode pausar apenas quando estiver `RUNNING`;
- pode continuar apenas quando estiver `PAUSED`;
- só pode concluir um intervalo quando estiver `RUNNING`.

Após um foco concluído:

```text
FOCUS
↓
SHORT_BREAK
```

Após uma pausa curta concluída:

```text
SHORT_BREAK
↓
FOCUS
```

Após o quarto foco concluído:

```text
FOCUS 4
↓
LONG_BREAK
```

## DEC-002 — Próximo intervalo não inicia automaticamente

**Data:** 05/10/2026

**Contexto:** ao terminar um intervalo, era necessário decidir se o próximo deveria começar imediatamente.

**Decisão inicial:** a fase muda, mas o status volta para `IDLE`.

Exemplo:

```text
FOCUS concluído
↓
SHORT_BREAK + IDLE
```

O usuário precisa iniciar explicitamente o próximo intervalo.

**Motivo inicial:** evitar que uma pausa ou novo foco seja iniciado sem que o usuário esteja preparado.

**Trade-off:** isso cria uma ação adicional para o usuário.

**Hipótese a validar com uso real:** o início manual pode ser mais controlável, mas também pode gerar cliques desnecessários.

Essa decisão poderá ser alterada posteriormente com base no uso real do produto.

## Testes automatizados

Foram implementados testes para verificar:

1. estado inicial da sessão;
2. início, pausa e continuação;
3. transição de foco para pausa curta;
4. bloqueio de pausa em sessão ociosa;
5. retorno ao foco após pausa curta;
6. pausa longa após quatro ciclos de foco.

Comando executado:

```bash
mvn test
```

Resultado:

```text
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Tempo registrado:

```text
3.648 s
```

## Evidência de comportamento

O fluxo principal validado pelos testes foi:

```text
FOCO 1
↓
PAUSA CURTA
↓
FOCO 2
↓
PAUSA CURTA
↓
FOCO 3
↓
PAUSA CURTA
↓
FOCO 4
↓
PAUSA LONGA
```

## Commit

O primeiro domínio funcional foi versionado no commit:

```text
e45f947 feat: implementa motor inicial do Pomodoro
```

O commit foi publicado com sucesso em `origin/main`.

Estado após o push:

```text
e45f947 (HEAD -> main, origin/main) feat: implementa motor inicial do Pomodoro
335105d docs: registra publicação inicial no GitHub
3ccc6bc docs: registra validação da fundação Maven
3e11ac6 chore: inicia estrutura do projeto FocusFlow
```

e:

```text
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean
```

## Correção documental posterior

Após o BLOCO 04 foi identificado que algumas cercas de código Markdown do documento `EXECUCAO.md` haviam ficado abertas durante a edição manual.

O problema era apenas documental e não afetava o código Java, os testes ou o build.

Este arquivo foi revisado integralmente para:

- fechar corretamente os blocos de código;
- restaurar a separação entre os BLOCOS 02, 03 e 04;
- manter consistência entre o roadmap e a decisão de separar fase e status;
- preservar as evidências reais já coletadas.

## Conclusão

O FocusFlow passou a possuir um motor inicial de domínio independente de interface gráfica e de cronômetro real.

As principais transições do Pomodoro estão representadas em código e cobertas por testes automatizados.

## Próximo passo

**BLOCO 05 — Configuração das durações do Pomodoro.**

Antes de implementar a contagem regressiva real, será definido onde devem ficar as durações de:

- 25 minutos de foco;
- 5 minutos de pausa curta;
- 15 a 20 minutos de pausa longa.

O objetivo será evitar números mágicos espalhados pelo código e manter as regras configuráveis e testáveis.
---

# BLOCO 05 — Configuração das durações do Pomodoro

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Retirar valores temporais e regras numéricas de dentro do motor da sessão e criar uma configuração explícita para o Pomodoro.

A intenção foi evitar números mágicos espalhados pelo domínio antes da implementação do cronômetro real.

## Problema identificado

A regra da pausa longa utilizava diretamente:

```java
completedFocusCycles % 4
```

Isso fazia com que o valor `4` ficasse incorporado à implementação de `PomodoroSession`.

As durações de foco e pausas também precisariam existir em algum lugar quando o cronômetro fosse implementado.

Espalhar valores como:

```text
25
5
15
4
```

por várias classes dificultaria manutenção, testes e futuras configurações.

## DEC-003 — Centralizar a configuração do Pomodoro

**Data:** 05/10/2026

**Contexto:**

O domínio precisava conhecer:

- duração do foco;
- duração da pausa curta;
- duração da pausa longa;
- quantidade de focos antes da pausa longa.

**Decisão:**

Criar o value object:

```text
PomodoroConfig
```

contendo:

```text
focusDuration
shortBreakDuration
longBreakDuration
focusCyclesBeforeLongBreak
```

## DEC-004 — Representar tempo com `java.time.Duration`

As durações passaram a utilizar:

```java
java.time.Duration
```

em vez de valores inteiros representando minutos.

### Motivo

Um valor como:

```java
25
```

não informa sua unidade.

Poderia representar:

- segundos;
- minutos;
- milissegundos.

Já:

```java
Duration.ofMinutes(25)
```

expressa explicitamente a intenção do domínio.

### Benefícios

- elimina ambiguidade de unidade;
- melhora legibilidade;
- facilita testes;
- permite futuras configurações em segundos sem alterar o modelo;
- integra-se às APIs de tempo do Java.

### Trade-off

`Duration` é mais elaborado que um simples `int`, mas fornece uma representação mais segura do conceito de tempo.

## Configuração padrão

Foi definida inicialmente:

```text
FOCUS                    25 minutos
SHORT_BREAK               5 minutos
LONG_BREAK               15 minutos
FOCUS CYCLES               4
```

A pausa longa permanece conceitualmente configurável entre 15 e 20 minutos.

Para o MVP inicial, o padrão escolhido foi:

```text
15 minutos
```

A configuração visual pelo usuário ficará para uma etapa posterior.

## Alteração em `PomodoroSession`

Antes:

```java
if (completedFocusCycles % 4 == 0)
```

Depois:

```java
if (completedFocusCycles
        % config.focusCyclesBeforeLongBreak() == 0)
```

Dessa forma, `PomodoroSession` deixou de possuir conhecimento direto sobre o número fixo de ciclos.

## Consulta da duração atual

Foi acrescentado:

```java
getCurrentDuration()
```

A duração retornada depende da fase atual:

```text
FOCUS       → focusDuration
SHORT_BREAK → shortBreakDuration
LONG_BREAK  → longBreakDuration
```

## Validação de regressão

Após introduzir `PomodoroConfig`, os seis testes existentes foram executados antes da criação dos novos testes.

Resultado:

```text
Tests run: 6
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Isso confirmou que a refatoração não quebrou as regras já existentes.

## Novos testes

Foram adicionados testes para validar:

1. duração padrão do foco;
2. duração da pausa curta após um foco;
3. duração da pausa longa após quatro ciclos.

Resultado final:

```text
Tests run: 9
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Tempo registrado:

```text
3.988 s
```

## Estrutura atual do domínio

```text
pomodoro/
├── PomodoroConfig.java
├── PomodoroPhase.java
├── PomodoroSession.java
└── PomodoroStatus.java
```

Testes:

```text
pomodoro/
└── PomodoroSessionTest.java
```

## Conclusão

As regras temporais deixaram de estar espalhadas pelo motor da sessão.

O FocusFlow agora possui uma configuração explícita e tipada para representar as durações e a quantidade de ciclos antes da pausa longa.

O domínio continua independente de:

- interface gráfica;
- JavaFX;
- banco de dados;
- relógio real.

## Próximo passo

**BLOCO 06 — Motor de contagem regressiva.**

O próximo problema será implementar a passagem real do tempo sem criar testes que precisem esperar 25 minutos para terminar.
---

# BLOCO 06 — Motor de contagem regressiva

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Criar um motor de contagem regressiva testável sem depender da passagem real do tempo.

O objetivo foi evitar testes que precisassem esperar 25 minutos para validar um Pomodoro completo.

## Problema identificado

Uma implementação ingênua poderia utilizar:

```java
Thread.sleep(...)
```

ou criar diretamente um scheduler dentro de `PomodoroSession`.

Isso traria dois problemas:

1. testes lentos;
2. acoplamento entre regras de domínio e infraestrutura de tempo.

## DEC-005 — O domínio recebe a passagem do tempo

**Data:** 05/10/2026

**Decisão:**

O motor não cria o relógio real.

Ele recebe explicitamente quanto tempo passou.

Exemplo:

```java
timer.elapse(Duration.ofSeconds(1));
```

ou, em testes:

```java
timer.elapse(Duration.ofMinutes(25));
```

## Motivo

Isso permite testar imediatamente um intervalo inteiro sem esperar 25 minutos.

Também mantém separado:

```text
REGRAS DO POMODORO
        ≠
INFRAESTRUTURA DE RELÓGIO
```

## Classe criada

```text
PomodoroTimer.java
```

Responsabilidades:

- manter o tempo restante;
- iniciar sessão;
- pausar;
- continuar;
- receber tempo decorrido;
- concluir automaticamente o intervalo quando o tempo termina;
- restaurar a duração correta da próxima fase;
- reiniciar a sessão.

## Regra de tempo excedente

Se faltarem:

```text
10 segundos
```

e o motor receber:

```text
15 segundos
```

os 5 segundos excedentes não são aplicados ao próximo intervalo.

Motivo:

Ao finalizar um foco, a próxima fase fica:

```text
SHORT_BREAK + IDLE
```

A pausa só inicia quando o usuário executar explicitamente o comando de início.

Essa decisão mantém coerência com a DEC-002.

## Testes criados

Arquivo:

```text
PomodoroTimerTest.java
```

Foram testados:

1. duração inicial do foco;
2. redução do tempo enquanto a sessão está rodando;
3. pausa impedindo redução do tempo;
4. mudança para pausa curta ao finalizar o foco;
5. tempo ignorado enquanto a sessão está ociosa;
6. rejeição de duração inválida;
7. reset restaurando a sessão inicial.

## Validação

Comando:

```bash
mvn test
```

Resultado:

```text
PomodoroTimerTest:
Tests run: 7
Failures: 0
Errors: 0

PomodoroSessionTest:
Tests run: 9
Failures: 0
Errors: 0
```

Total:

```text
Tests run: 16
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

## Estrutura atual

```text
pomodoro/
├── PomodoroConfig.java
├── PomodoroPhase.java
├── PomodoroSession.java
├── PomodoroStatus.java
└── PomodoroTimer.java
```

Testes:

```text
pomodoro/
├── PomodoroSessionTest.java
└── PomodoroTimerTest.java
```

## Conclusão

O FocusFlow agora possui um motor de contagem regressiva desacoplado do relógio real.

Isso permite que os testes simulem minutos ou horas em milissegundos de execução.

## Próximo passo

**BLOCO 07 — Relógio real de 1 segundo.**

O próximo passo será criar uma camada responsável por gerar ticks reais de tempo e conectá-la ao `PomodoroTimer`, sem mover essa responsabilidade para o domínio.

---

# BLOCO 07 — Relógio real de 1 segundo

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Adicionar uma camada responsável pela passagem real do tempo sem colocar threads ou agendamento dentro das regras do domínio.

## Problema identificado

O `PomodoroTimer` já sabia processar tempo decorrido:

```java
timer.elapse(Duration.ofSeconds(1));
```

Porém ainda não existia nenhum componente responsável por produzir esses pulsos automaticamente durante a execução real do aplicativo.

Uma alternativa seria colocar um `ScheduledExecutorService` diretamente dentro de `PomodoroSession` ou `PomodoroTimer`.

Essa abordagem foi descartada porque misturaria:

```text
regras do Pomodoro
+
controle do tempo restante
+
infraestrutura de threads
```

## DEC-006 — Relógio real como infraestrutura

**Data:** 05/10/2026

**Decisão:**

Criar uma classe separada:

```text
PomodoroClock
```

responsável exclusivamente por gerar um tick a cada segundo.

Arquitetura:

```text
ScheduledExecutorService
          │
          │ 1 segundo
          ▼
   PomodoroClock
          │
          ▼
   PomodoroTimer
          │
          ▼
  PomodoroSession
```

## Responsabilidades

### `PomodoroSession`

Mantém as regras do Pomodoro.

### `PomodoroTimer`

Mantém o tempo restante e processa tempo decorrido.

### `PomodoroClock`

Produz a passagem real do tempo.

## Estratégia de thread

Foi utilizado:

```java
ScheduledExecutorService
```

com uma única thread chamada:

```text
focusflow-pomodoro-clock
```

A thread foi configurada como:

```text
daemon
```

para não impedir o encerramento da aplicação caso seja a única thread restante.

## Tick

O intervalo utilizado pelo relógio é:

```java
Duration.ofSeconds(1)
```

O método:

```java
tick()
```

permaneceu com visibilidade de pacote.

### Motivo

A aplicação não precisa disparar ticks manualmente.

Porém os testes localizados no mesmo pacote conseguem chamar:

```java
clock.tick();
```

sem esperar um segundo real.

Isso permite testar a lógica sem introduzir:

```java
Thread.sleep(...)
```

nos testes.

## Controle do relógio

Foram implementados:

```text
start()
stop()
isRunning()
close()
```

`PomodoroClock` também implementa:

```java
AutoCloseable
```

permitindo uso seguro em:

```java
try (PomodoroClock clock = new PomodoroClock(timer)) {
    ...
}
```

## Testes criados

Arquivo:

```text
PomodoroClockTest.java
```

Foram validados:

1. um tick reduz exatamente um segundo;
2. o tempo não diminui quando o Pomodoro está pausado;
3. ticks suficientes concluem o foco;
4. o relógio pode ser iniciado e parado.

## Configuração reduzida para testes

Para evitar testes demorados, foi usada uma configuração como:

```text
FOCUS        3 segundos
SHORT_BREAK  2 segundos
LONG_BREAK   4 segundos
CICLOS       4
```

Essa configuração existe somente no teste.

As configurações padrão do produto permanecem:

```text
FOCUS        25 minutos
SHORT_BREAK   5 minutos
LONG_BREAK   15 minutos
```

## Validação completa

Comando:

```bash
mvn test
```

Resultados:

```text
PomodoroClockTest
Tests run: 4

PomodoroTimerTest
Tests run: 7

PomodoroSessionTest
Tests run: 9
```

Total:

```text
Tests run: 20
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Tempo total:

```text
4.775 s
```

## Estrutura atual

```text
pomodoro/
├── PomodoroClock.java
├── PomodoroConfig.java
├── PomodoroPhase.java
├── PomodoroSession.java
├── PomodoroStatus.java
└── PomodoroTimer.java
```

Testes:

```text
pomodoro/
├── PomodoroClockTest.java
├── PomodoroSessionTest.java
└── PomodoroTimerTest.java
```

## Decisão adiada

O relógio utiliza uma thread própria.

Quando JavaFX for introduzido, a interface gráfica também possuirá sua própria thread.

A forma segura de atualizar a interface a partir do relógio será decidida somente quando essa necessidade existir.

Não será antecipada complexidade relacionada à GUI neste momento.

## Conclusão

O FocusFlow agora possui:

```text
regras do Pomodoro
        +
controle de tempo
        +
relógio real
```

mantidos em responsabilidades separadas e cobertos por testes automatizados.

## Próximo passo

**BLOCO 08 — Primeira execução visível no terminal.**

Antes de introduzir JavaFX, será criada uma pequena aplicação executável que permita observar o FocusFlow funcionando em tempo real pelo terminal.

---

# BLOCO 08 — Primeira execução visível no terminal

**Data:** 05/10/2026

**Status:** ✅ Concluído

## Objetivo

Criar a primeira aplicação executável do FocusFlow integrando o domínio, o timer e o relógio real antes da introdução da interface gráfica.

Até este ponto, os componentes estavam validados principalmente por testes automatizados.

Neste bloco, o objetivo foi observar o sistema funcionando como uma aplicação real.

## Aplicação criada

Foi criado:

```text
src/main/java/io/github/aguinaldo1/focusflow/cli/FocusFlowCli.java
```

A aplicação de demonstração integra:

```text
FocusFlowCli
      │
      ▼
PomodoroClock
      │
      ▼
PomodoroTimer
      │
      ▼
PomodoroSession
```

## Configuração de demonstração

Para evitar esperar 25 minutos durante a validação manual, foi utilizada:

```text
FOCUS        10 segundos
SHORT_BREAK   5 segundos
LONG_BREAK    8 segundos
CICLOS        4
```

Essa configuração não altera os valores padrão do produto.

Os valores padrão permanecem:

```text
FOCUS        25 minutos
SHORT_BREAK   5 minutos
LONG_BREAK   15 minutos
```

## Execução

Após compilação:

```bash
mvn test
```

o programa foi executado com:

```bash
java -cp target/classes \
io.github.aguinaldo1.focusflow.cli.FocusFlowCli
```

A execução apresentou:

```text
=================================
          FOCUSFLOW
=================================

Modo demonstração

Foco:        10 segundos
Pausa curta:  5 segundos
Pausa longa:  8 segundos

Iniciando foco...

FOCO | 00:01

✓ Foco concluído.
Próxima fase: SHORT_BREAK
Duração: 00:05

O próximo intervalo permanece parado até o usuário iniciá-lo.
```

## Atualização da mesma linha do terminal

Durante a execução foi utilizado:

```java
System.out.printf("\rFOCO | %s", ...)
```

O caractere:

```text
\r
```

faz com que os valores sejam sobrescritos na mesma linha.

Durante a execução são apresentados valores como:

```text
00:10
00:09
00:08
...
00:01
```

Ao copiar a saída do terminal, normalmente permanece apenas o último valor renderizado.

## Uso de `Thread.sleep(100)`

A aplicação CLI contém:

```java
Thread.sleep(100);
```

Esse `sleep` não controla a passagem do tempo do Pomodoro.

Sua responsabilidade é apenas evitar que o loop de apresentação consulte o estado continuamente consumindo CPU desnecessariamente.

A passagem do tempo continua sendo responsabilidade de:

```text
PomodoroClock
```

Portanto:

```text
PomodoroClock
→ produz os ticks de tempo

FocusFlowCli
→ observa e apresenta o estado
```

## Validação automatizada

Antes da execução manual, todos os testes foram executados novamente.

Resultado:

```text
Tests run: 20
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Também foi confirmada a compilação de:

```text
7 source files
```

## Validação temporal manual

A execução também foi medida utilizando:

```bash
time java -cp target/classes \
io.github.aguinaldo1.focusflow.cli.FocusFlowCli
```

Resultado:

```text
real    0m11.759s
user    0m0.393s
sys     0m0.442s
```

A configuração de foco utilizada era de:

```text
10 segundos
```

O tempo total do processo inclui também:

- inicialização da JVM;
- criação dos componentes;
- scheduler;
- apresentação no terminal;
- encerramento do processo.

O resultado foi considerado coerente com a configuração utilizada.

## Evidência funcional

A execução comprovou a integração:

```text
aplicação inicia
↓
sessão inicia
↓
relógio gera ticks reais
↓
timer reduz o tempo restante
↓
foco termina
↓
sessão muda para SHORT_BREAK
↓
timer carrega 5 segundos
↓
status retorna para IDLE
```

Essa última etapa confirma também a aplicação da DEC-002:

```text
o próximo intervalo não inicia automaticamente
```

## Marco do projeto

A partir deste bloco, o FocusFlow deixou de ser apenas um conjunto de classes de domínio cobertas por testes.

O sistema passou a possuir uma aplicação executável capaz de demonstrar a contagem regressiva real.

## Próximo passo

**BLOCO 09 — Controles interativos pelo terminal.**

Antes da introdução do JavaFX, será validada a interação real do usuário com operações como:

```text
iniciar
pausar
continuar
resetar
sair
```

Essa etapa também permitirá avaliar a interação entre a thread responsável pelo relógio e a thread que recebe comandos do usuário.
