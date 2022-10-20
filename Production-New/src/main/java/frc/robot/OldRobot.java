package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;
import edu.wpi.first.wpilibj.Ultrasonic;


import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

// https://software-metadata.revrobotics.com/REVLib.json
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.Spark;




public class OldRobot extends TimedRobot {

  //Motors
  // TalonSRX talonLeft = new TalonSRX(5);
  // VictorSPX victorLeft = new VictorSPX(4);

  // TalonSRX talonRight = new TalonSRX(3);
  // VictorSPX victorRight = new VictorSPX(6);

  PWMVictorSPX leftMotor = new PWMVictorSPX(2);
  PWMVictorSPX rightMotor = new PWMVictorSPX(3);
  public DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor); // insert drive here


  CANSparkMax flyWheelMotor = new CANSparkMax(10, MotorType.kBrushless);
  RelativeEncoder flyWheelEncoder = flyWheelMotor.getEncoder();

  CANSparkMax feederMotor = new CANSparkMax(11, MotorType.kBrushless);

  PWMVictorSPX intakeMotor = new PWMVictorSPX(5); // assuming 1 
  PWMVictorSPX climbMotor = new PWMVictorSPX(4); // This is correct
  // PWMVictorSPX feederMotor = new PWMVictorSPX(5);

  

  //Pneumatics
  Compressor comp = new Compressor(0, PneumaticsModuleType.CTREPCM);
  DoubleSolenoid boost = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0,7);
  // DoubleSolenoid lock = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1,7);

  // DoubleSolenoid floats = new DoubleSolenoid(4,3);
  // DoubleSolenoid shield1 = new DoubleSolenoid(6,1);
  // DoubleSolenoid shield2 = new DoubleSolenoid(0,7);

  //data
  private final Timer timer = new Timer();
  //private ADXRS450_Gyro gyro = new ADXRS450_Gyro();
 // private double angle = 0;

  //public boolean climbUp = false;
  //control
  private final XboxController c1 = new XboxController(0);
  private final XboxController c2 = new XboxController(1);
  public double intakeSpeed = 0.0;
  public double climbSpeed = 0.0;
  public double feederSpeed = 0.0;
  int flyWheelMode = 0;

  Ultrasonic ultrasonic = new Ultrasonic(1, 2); //1 AND 2 correspond to DIO pins


  // stats toggle the smartdashboard
  boolean stats = true;



 //initalize trigger buttons

  // Joystick stick = 
 
//  final JoystickButton l2 = new JoystickButton(c, 9);
//  final JoystickButton r2 = new JoystickButton(c, 10);


  @Override
  public void robotInit() {
    // comp.setClosedLoopControl(false);
    // arm.set(Value.kForward);
    
    // lock.set(Value.kReverse);
    //gyro.calibrate();

    boost.set(Value.kReverse);
    flyWheelMotor.set(0);
    climbMotor.set(0);
    intakeMotor.set(intakeSpeed);


  
    rightMotor.setInverted(true);

    Ultrasonic.setAutomaticMode(true);

    /**
     * The RestoreFactoryDefaults method can be used to reset the configuration parameters
     * in the SPARK MAX to their factory default state. If no argument is passed, these
     * parameters will not persist between power cycles
     */
    flyWheelMotor.restoreFactoryDefaults();
  
  
  }

  @Override
  public void robotPeriodic() {
    // angle = gyro.getAngle();
    // angle%=360;
    // if (angle < -180)
    //   angle+=360;
    // else if (angle > 180)
    //   angle-=360;
  }

  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
  }

  private void scoreTaxi(){
    // score 2 points by driving off the starting tarmac
    if (timer.get() < 1.5){
      drive.tankDrive(0.6, 0.6);
    }else{
      drive.stopMotor();
    }
  }

  private void shoot(){
    if (timer.get() < 5){
      flyWheelMotor.set(0.3);
    }else{
      flyWheelMotor.stopMotor();
    }
  }

  @Override
  public void autonomousPeriodic() {
    // scoreTaxi();
    shoot();
  }

  @Override
  public void teleopInit() {
    // gyro.reset();
  }   

  // double aTarget = 0, bTarget = 0; // a for a button, b for bumpers"
  boolean compressing = false;


  @Override
  public void teleopPeriodic() {

    // DRIVING SYSTEM
    double forwardSpeed = c1.getLeftY();
    double turnSpeed = c1.getLeftX();

    // DO NOT TOUCH
    drive.arcadeDrive(forwardSpeed, -turnSpeed, true); // DO NOT TOUCH
    // DO NOT TOUCH ^


    if(c1.getBButtonPressed()) // toggle sprint
      boost.toggle();


    // INTAKE SYSTEM |   CONTROLLER 2 right trigger suck, left trigger spit

  
    double rightTrigger = c2.getRightTriggerAxis();
    double leftTrigger = c2.getLeftTriggerAxis();

    intakeSpeed = 0.0;
    if(rightTrigger > 0){
      intakeSpeed = rightTrigger;
    }else if (leftTrigger > 0){
      intakeSpeed = -leftTrigger;
    }



    // FEEDER SYSTEM |   CONTROLLER 2 y & a hold down to spin

    
    
    if (c2.getAButton()){
      // activiate right trigger
      feederSpeed = 0.5;
    }else if (c2.getYButton()){
      feederSpeed = -0.5;
    } else{
      feederSpeed = 0.0;
    }


    // FLYWHEEL SYSTEM |  CONTROLLER 2 controlled by bumpers, RIGHT BUMPER increment speed, capped at highest speed, LEFT BUMPER reset speed to 0

    
    double[] flyWheelSpeeds = {0.0, 0.5, 0.85}; // speeds when driving around, low goal, and high goal
    // controls flywheel speed, lower speed is 10% speed, max speed is 100% speed
    if(c2.getRightBumperPressed() && flyWheelMode < 2) // increment flywhell speed when right bumper pressed
      flyWheelMode++;
    if(c2.getLeftBumperPressed())
      flyWheelMode = 0;
    
    
    // CLIMBER SYSTEM |   CONTROLLER 1 controlled by Y & A
    
    climbSpeed = 0.0;
    if(c1.getYButton()) // up
      climbSpeed = 0.8;
    if(c1.getAButton()) // down
      climbSpeed = -0.6;


    


    if(ultrasonic.getRangeMM()>2000)
    {
      //LIGHTS ARE GREEN
    }

    // if(c.getLeftBumperPressed()) // decrement flywheel speed when left bumper pressed.
      // lock.set(Value.kForward);

    if (stats){
      SmartDashboard.putNumber("Flywheel Encoder", flyWheelEncoder.getVelocity());
    }

    intakeMotor.set(intakeSpeed); // PWM 
    feederMotor.set(feederSpeed); // CAN
    flyWheelMotor.set(-flyWheelSpeeds[flyWheelMode]); //CAN
    intakeMotor.set(intakeSpeed); //PWM
    climbMotor.set(climbSpeed); //PWM
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
