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

### Entity Component System (ECS)

The game uses a custom ECS architecture for better performance and maintainability:

- **Entities**: Basic containers with unique IDs
- **Components**: Pure data containers (e.g., Position, Movement, Render)
- **Systems**: Logic processors that operate on entities with specific components

### Key Systems

- **RenderSystem**: Handles all rendering operations
- **MovementSystem**: Processes entity movement
- **NetworkSystem**: Manages multiplayer synchronization with client-side prediction
- **InputSystem**: Processes user input
- **CameraSystem**: Handles game view and viewport
- **PhysicsSystem**: Manages rigid body physics and collision detection
- **AISystem**: Controls bot behavior and boid simulation
- **AudioSystem**: Handles sound synthesis and playback

## Development

The project follows these key principles:

- YAGNI (You Aren't Gonna Need It)
- KISS (Keep It Simple, Stupid)
- Multiplayer-first design
- Incremental feature implementation

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- Original diep.io game for inspiration
- LWJGL team for their excellent Java game library 