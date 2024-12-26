# JDiep - A Diep.io Clone in Java

A multiplayer-first implementation of diep.io using Java and LWJGL, built with a custom Entity Component System (ECS) architecture. This project focuses on clean, maintainable code while avoiding over-engineering.

## Features (current and planned)

### Core Architecture
- Custom ECS (Entity Component System) architecture
- Multiplayer-first design with stability-focused netcode
- Built with LWJGL (Lightweight Java Game Library)
- Real-time gameplay with client-side prediction
- Debug visualization tools and performance monitoring

### Physics & Gameplay Enhancements
- Advanced rigid body physics system
  - Broad and narrow phase collision detection
  - Quadtree spatial partitioning
  - GJK (Gilbert-Johnson-Keerthi) collision algorithm
- Boid behavior for drone AI
- Physical modeling audio synthesis
- Hardcore mode with optional UI disable

### Extended Game Mechanics
- New tank classes:
  - Physics manipulator with powerful turret for pushing
  - Stealth class with food item disguise ability
- Enhanced kill feed with mass transfer visualization
- Special indicators for leaderboard kills
- Sandbox mode for experimentation

### Multiplayer & Community
- Community server support
- Customizable server features
- Enhanced spectating system:
  - Player follow mode
  - Free camera mode
  - Optional stats visualizations for mass and kills
  - Zoom in/out for all spectator modes
- Chat system with admin commands
- Account system tracking:
  - Mass history
  - Kill statistics
  - Experience points
  - Lifetime achievements
  - Other relevant statistics

### AI & Debug Features
- Sophisticated AI bot system
- Comprehensive debug toolkit:
  - Performance graphs
  - Real-time metrics
  - Hitbox visualizations
  - Vector displays
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