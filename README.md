# Projet MVC — Design Patterns et Application Interactive

**Jeu d'occupation maximale de formes**

<img width="251" height="155" alt="Captura de pantalla 2026-07-14 a las 12 21 14" src="https://github.com/user-attachments/assets/69044351-7aa0-4580-a35a-754b69a8da1b" />

An interactive Java Swing game built around the **MVC (Model–View–Controller)** architecture, showcasing seven+ classic design patterns working together in a real, non-trivial application: shape placement, undo/redo, AI opponents, and a 2-player mode.

Master's project (M1 Informatique – Algorithmique et Systèmes Intelligents), Université de Caen Normandie, 2025–2026.

**Code source :** [github.com/britek2001/MVC](https://github.com/britek2001/MVC)

**Réalisé par :** Mohammed Yassine Britel, Abdelkerim Hamit Mahamat, Taiwan Li
**Encadrant :** Yann Mathet

## About the game

The system generates **red shapes** at random positions to act as fixed obstacles. The player then creates, moves, resizes, and deletes **blue shapes** by drag-and-drop, aiming to occupy as much area as possible.

- Blue shapes may never intersect red shapes.
- In **solo mode**, blue shapes may not overlap each other either.
- In **2-player mode**, blue-blue overlap is allowed.
- Every shape must stay entirely within the play area.
- A maximum of 4 blue shapes can be placed per level.
- Invalid moves/resizes snap back to the shape's last valid position/size.

Red shapes are visible for a limited time (set per level); once the timer expires they disappear and the player places blue shapes. In solo mode, failing to place 4 shapes before the timer runs out ends the game in a loss.

### Levels

| Level | Red shapes | Time | Difficulty |
|---|---|---|---|
| 1 | 2 | 10s | Facile |
| 2 | 3 | 10s | Moyen |
| 3 | 4 | 8s | Difficile |
| 4 | 5 | 8s | Très difficile |
| 5 | 6 | 6s | Extrême |

### Scoring

- Rectangle area: `width × height`
- Circle area: `π × radius²`
- Total score: sum of the area of all placed blue shapes

## Architecture

Strict **MVC** separation:

- **Model** — shapes, game rules, score, state
- **View** — graphical rendering and UI
- **Controller** — mouse/user event handling

### Package structure

```
model.games       → core game orchestration
model.game        → game state and configuration
model.shapes       → GameShape (abstract), Circle, Rectangle
model.commands     → Command interface and concrete commands
model.strategy     → shape-generation strategies (random, AI, 2-player)
model.view         → GameView, GamePainter
model.controller   → ControleurSouris
```

## Design patterns

This project deliberately layers multiple patterns to solve distinct problems, rather than using a single pattern everywhere.

| Pattern | Purpose | Key classes |
|---|---|---|
| **Observer** | Synchronizes `GameModel` and `GameView` — the view redraws whenever the model changes | `GameModel` (Observable), `GameView` (Observer) |
| **State** | Manages mouse-interaction modes (select, create, move, resize) without long conditionals | `EtatInteraction`, `EtatSelection`, `EtatCreationRectangle`, `EtatCreationCercle`, `EtatMoveShape`, `EtatResizeShape` |
| **Command** | Encapsulates every user action to support undo/redo via two stacks | `Command`, `CreateShapeCommand`, `DeleteShapeCommand`, `MoveShapeCommand`, `ResizeShapeCommand`, `CommandHManager` |
| **Strategy** | Makes red-shape generation interchangeable (random, AI, 2-player) | `ShapeGenerationStrategy`, `RandomGenerationStrategy`, `AIPlayerStrategy`, `TwoPlayerStrategy` |
| **Template Method** *(partial)* | Standardizes the execute → store-in-history flow shared by all commands | `CommandHManager.executeAndStore(Command)` |
| **Factory** *(partial)* | Centralizes shape creation from click coordinates | `ClickPlacementStrategy.createRectangleFromClicks()`, `createCircleFromClicks()` |
| **Composite** *(partial)* | Lets the model treat every shape uniformly through one interface | `GameShape` interface, implemented by `Rectangle` and `Circle` |
| **Builder** | Assembles configurable game-flow phase chains (classic, memory, AI modes) | `GameFlowBuilder` |
| **Chain of Responsibility** | Reused at two levels: validating a resize (factor, intersection, game-area checks) and pipelining AI shape generation (analyze → decide size → generate) | `ValidateResizeFactorValidator`, `ValidateIntersectionValidator`, `ValidateGameAreaValidator`; `AIShapeGenerationBuilder`, `AIAnalyzeGameStatePhase`, `AIDecideShapeSizePhase`, `AIGenerateShapesPhase` |
| **Abstract Factory** | Manages visual themes (Dark / Light) | theme factory classes |

### AI opponent

The `AIPlayerStrategy` orchestrates a high-level decision loop (mode, score, rounds) while delegating shape generation to a Chain-of-Responsibility pipeline. The AI dynamically adjusts blue-shape size based on available space:

```
surfaceDisponible = largeurPanneau × hauteurPanneau
pressionEspace = 1 − surfaceDisponible / 500000
tailleAjustee = tailleBase × (1 − pressionEspace × 0.5)
```

If the AI fails to place a shape 50 times in a row, the target size shrinks by 15% (down to a 5px floor) to increase the odds of finding a valid spot. Past 50% surface coverage, it switches to a more defensive (smaller-shapes) strategy.

### Turn management & concurrency

2-player and AI modes rely on Java monitor synchronization (`synchronized`, `wait()`, `notifyAll()`) via `TurnCoordinator`, `PlayerTurnState`, and `TurnSignal`, so the Swing UI stays responsive while turns are coordinated across threads — the AI can "think" without blocking rendering, and human/AI actions never interfere with each other.

## Testing

**71 JUnit tests** across all modules, covering:

- Command execution, undo, redo, and history-stack behavior (`CommandHManagerTest`, `CommandTest`)
- Model rules: shape placement, intersection checks, scoring, level progression, timers, 2-player mode (`GameModelTest`)
- Geometry: containment, intersection (circle/circle, circle/rectangle, rectangle/rectangle), area, zone classification (`ShapeTest`)
- Strategy interchangeability (`StrategyTest`)
- Turn synchronization (`TurnCoordinatorTest`)
- View components: control panel callbacks, theme/button factories, menu logic, painter behavior (`ViewTest`)
- End-of-game behavior for both solo/AI and 2-player modes

## Code quality

Static analysis was run with a SonarQube-type inspection tool:

| Metric | Result |
|---|---|
| Security | A — no vulnerabilities detected |
| Reliability | B — 3 potential issues identified |
| Maintainability | A — 209 code smells |
| Hotspots reviewed | 100% |
| Test coverage (as measured by the tool) | 0.0% *(not wired up — 71 JUnit tests exist but weren't integrated into the Sonar report)* |
| Duplication | 1.8% |

## Getting started

### Prerequisites

- Java (JDK)
- Maven

### Compile and run

```bash
git clone https://github.com/britek2001/MVC.git
cd MVC
mvn clean compile
java -cp target/classes mvc.Main
```

### Run tests

```bash
mvn clean test
```

## Future improvements

- Wire up test coverage reporting in SonarQube
- Address the 3 reliability issues and reduce code smells
- Expand the shape system (triangles, polygons) via the existing Composite/Factory structure

## References

- E. Gamma, R. Helm, R. Johnson, J. Vlissides — *Design Patterns: Elements of Reusable Object-Oriented Software*, Addison-Wesley, 1994
- Krasner, G., Pope, S. — *A Description of the Model-View-Controller User Interface Paradigm*, Journal of Object-Oriented Programming, 1988
- [Java Platform Documentation](https://docs.oracle.com/javase/8/docs/)
- [PlantUML](https://plantuml.com)
- [SonarQube Documentation](https://www.sonarsource.com/products/sonarqube/)
