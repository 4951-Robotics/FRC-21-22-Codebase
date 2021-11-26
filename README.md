# FRC-21-22-Codebase


## Tall Bot Specs
### Motors
PWMVictorSPX Motor Controllers on **PWN ports** 3,2

`private DifferentialDrive drive = new DifferentialDrive(new PWMVictorSPX(3), new PWMVictorSPX(2));` 

<br/>

### Gyro
Single axis gyro? on **Analog port** 0

`private AnalogGyro gyro = new AnalogGyro(0);`


<br/>

### Encoders
Encoder on left side of drive train (from forward perspective, you see robot's back side) on **DIO pins (0, 1)** 

`private final Encoder encoder = new Encoder(0, 1);`

--- 
## Trapezoid Bot 

### Motors
Motors connected by CAN bus.


... To be documented.
