# FocusFlow — Interface HUD

## Objetivo

Evoluir a interface do FocusFlow para uma identidade visual própria de assistente de produtividade com inspiração em interfaces futuristas e sistemas de IA.

A referência visual utiliza:

- fundo escuro;
- azul petróleo;
- cyan luminoso;
- elementos HUD;
- painéis tecnológicos;
- indicadores circulares;
- contraste elevado;
- assistente visual original.

A identidade deve permanecer original, sem reproduzir personagens ou interfaces protegidas.

---

# BLOCO 20A — Fundação visual

## 20A.1 — Tema HUD

**Status:** ✅ Concluído

### Alterações

Foi criada a folha de estilos:

`src/main/resources/styles/focusflow-hud.css`

A aplicação passou a utilizar uma paleta central baseada em:

- fundo principal: `#030812`;
- azul petróleo: `#071525`;
- cyan principal: `#00D9FF`;
- azul energético: `#138BFF`;
- laranja de atenção: `#FF8A28`;
- verde de sucesso: `#23E6B1`;
- vermelho destrutivo: `#FF5067`.

O CSS foi conectado à `Scene` principal do `FocusFlowApp`.

Também foram adaptados:

- fundo da aplicação;
- botões;
- campos;
- ComboBox;
- textos;
- feedback;
- atalhos;
- timer;
- indicação de fase;
- ProgressDonut.

### Pomodoro

O cronômetro passou a utilizar contraste adequado ao tema escuro.

FOCO:

- timer claro;
- fase em cyan.

PAUSA:

- timer em verde claro;
- fase em verde/cyan.

### ProgressDonut

O indicador de progresso passou a utilizar:

- fundo azul petróleo;
- progresso cyan;
- percentual claro;
- brilho cyan discreto.

### Validação

- 78 testes automatizados passando;
- CSS carregado corretamente;
- aplicação inicializada normalmente;
- timer funcionando;
- controles funcionando;
- objetivo funcionando;
- persistência preservada;
- percentual legível;
- pausa visualmente diferenciada;
- nenhuma regressão funcional identificada.

## Resultado

O FocusFlow deixou o visual JavaFX padrão e passou a possuir a primeira camada da identidade HUD futurista.

Esta etapa define a base para a evolução dos painéis e da composição da interface.

---

# BLOCO 20A.2 — Painéis HUD

**Status:** ✅ Concluído

## Objetivo

Evoluir os componentes principais do FocusFlow para uma aparência modular inspirada em interfaces HUD, sem alterar regras de negócio ou comportamento da aplicação.

## Alterações

Foram estilizados como módulos HUD:

- painel de objetivos;
- painel do Pomodoro;
- controles do Pomodoro;
- botão Histórico.

Os controles passaram a utilizar cores semânticas:

- verde para ações positivas;
- laranja para pausa e atenção;
- cyan/azul para ações principais;
- vermelho para ações destrutivas;
- tons neutros para ações secundárias.

O painel de objetivos passou a manter apenas o título `OBJETIVO ATUAL`.

Os títulos decorativos:

`OBJETIVOS`

`PLANEJAMENTO`

`SESSÃO DE FOCO`

foram removidos após validação visual para preservar uma interface mais limpa e compacta.

A janela principal foi ajustada para `430 x 520`, evitando corte dos controles após a introdução dos cards e espaçamentos HUD.

## Decisão visual

A identidade do FocusFlow deve seguir o princípio:

**futurista, tecnológico e limpo.**

O estilo HUD deve ser comunicado principalmente por:

- cores;
- bordas;
- iluminação;
- contraste;
- indicadores;
- formas;
- hierarquia visual.

Evitar excesso de textos decorativos e elementos sem função.

## Validação

- 78 testes automatizados passando;
- painel de objetivos funcionando;
- painel do Pomodoro funcionando;
- controles funcionando;
- Histórico visível;
- nenhum componente cortado;
- timer centralizado;
- ProgressDonut preservado;
- nenhuma regressão funcional identificada.

## Resultado

A interface passou a possuir módulos HUD visualmente separados e semanticamente consistentes, mantendo o FocusFlow compacto e legível.
