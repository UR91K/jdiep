# JDiep - A Diep.io Clone in Java

A multiplayer-first implementation of diep.io using Java and LWJGL, built with a custom Entity Component System (ECS) architecture. This project focuses on clean, maintainable code while avoiding over-engineering.

## Features

### Current Features

#### Core Architecture
- Custom ECS (Entity Component System) architecture
  - Entity management with unique IDs
  - Component-based data storage
  - System-based logic processing
- LWJGL (Lightweight Java Game Library) integration
- Basic game loop with delta time
- Input handling system

#### Rendering System
- OpenGL-based rendering pipeline
- Shape rendering (circles, rectangles, polygons)
- Color and outline support
- Layer-based rendering system
- Camera system with basic movement

#### Game Systems
- Transform system with parent-child relationships
- Movement system with velocity and acceleration
- Basic tank implementation
  - Tank body and turret components
  - Transform hierarchies
- Debug visualization tools
  - Performance monitoring
  - Entity state visualization
  - Debug drawing capabilities

### Planned Features

#### Core Mechanics
- Multiplayer support with client-side prediction
- Advanced rigid body physics system
  - Broad and narrow phase collision detection
  - Quadtree spatial partitioning
  - GJK (Gilbert-Johnson-Keerthi) collision algorithm
- Food system implementation
- Drone AI with boid behavior
- Physical modeling audio synthesis

#### Game Content
- New tank classes:
  - Physics manipulator with powerful turret
  - Stealth class with food disguise ability
- Enhanced kill feed with mass transfer visualization
- Special indicators for leaderboard kills
- Sandbox mode

#### Multiplayer & Community
- Community server support
- Customizable server features
- Enhanced spectating system:
  - Player follow mode
  - Free camera mode
  - Stats visualizations
- Chat system with admin commands
- Account system with statistics tracking

#### AI & Advanced Debug
- Sophisticated AI bot system
- Comprehensive debug toolkit expansion:
  - Advanced performance graphs
  - Network statistics
  - AI behavior visualization
- Mod support for community extensions

## 🏗️ Architecture

### Core Libraries

- **Ashley ECS**: Core entity component system
- **LWJGL**: OpenGL rendering and GLFW input
- **JBox2D**: Physics simulation
- **Netty**: Networking layer
- **OpenAL**: Audio system
- **imGUI**: Debug interface
- **tinylog**: Logging system

### Module Organization

```
Core
├── Engine (Ashley ECS + basic systems)
├── Physics (JBox2D wrapper)
├── Rendering (LWJGL/OpenGL)
├── Input (GLFW)
├── Audio (OpenAL)
└── Network (Netty)

Modules
├── Game (game-specific systems)
├── Debug (imGUI integration)
├── Config (configuration management)
└── Events (event bus system)
```

### Design Principles

#### Component Design
- Pure data containers
- No behavior in components
- Flat component hierarchy
- Composition over inheritance

#### System Design
- Single responsibility per system
- Event-based communication
- Configurable behavior
- Easy enable/disable support

#### Core Principles
- Modularity First: Systems should be easily swappable
- Simple but Extensible: Core systems provide basic functionality with clear extension points
- Data-Oriented: Focus on data transformation rather than object behavior
- Event-Driven: Loose coupling through event-based communication

### Development Guidelines

The project follows these key principles:

- YAGNI (You Aren't Gonna Need It)
- KISS (Keep It Simple, Stupid)
- Composition over Inheritance
- Data-Oriented Design
- Event-Driven Architecture

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- Original diep.io game for inspiration
- LWJGL team for their excellent Java game library 