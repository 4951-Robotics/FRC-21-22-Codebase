package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;

import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;



public class Robot extends TimedRobot {

  //Motors
  // TalonSRX talonLeft = new TalonSRX(5);
  // VictorSPX victorLeft = new VictorSPX(4);

  // TalonSRX talonRight = new TalonSRX(3);
  // VictorSPX victorRight = new VictorSPX(6);

  PWMVictorSPX leftMotor = new PWMVictorSPX(2);
  PWMVictorSPX rightMotor = new PWMVictorSPX(3);
  public DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor); // insert drive here

  // PWMSparkMax flyWheelMotor = new PWMSparkMax(0); ;//INCLUDE PORT NAME HERE

  CANSparkMax flyWheelMotor = new CANSparkMax(0, MotorType.kBrushless);
  

  PWMVictorSPX intakeMotor = new PWMVictorSPX(1); // assuming 1 
  PWMVictorSPX climbMotor = new PWMVictorSPX(4); // This is correct


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
  private double angle = 0;

  public boolean climbUp = false;
  //control
  private final XboxController c = new XboxController(0);
  public double intakeSpeed = 0.7;
  public double climbSpeed = 0.0;



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

  @Override
  public void autonomousPeriodic() {
    scoreTaxi();
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
    double forwardSpeed = c.getLeftY();
    double turnSpeed = c.getLeftX();

    // DO NOT TOUCH
    drive.arcadeDrive(forwardSpeed, -turnSpeed, true); // DO NOT TOUCH
    // DO NOT TOUCH ^

    if(c.getBButtonPressed()) // toggle sprint
      boost.toggle();


    // FEEDER SYSTEM




    // FLYWHEEL SYSTEM

    int flyWheelMode = 0;
    double[] flyWheelSpeeds = {0.0, 0.5, 0.95}; // speeds when driving around, low goal, and high goal
    // controls flywheel speed, lower speed is 10% speed, max speed is 100% speed
    if(c.getRightBumperPressed()) // increment flywhell speed when right bumper pressed
      flyWheelMode++;
    if(c.getLeftBumperPressed())
      flyWheelMode--;

    flyWheelMode %= 3;
    
    
    // CLIMBER SYSTEM
    
    climbSpeed = 0.0;
    if(c.getYButton()) // up
      climbSpeed = 0.8;
    if(c.getAButton()) // down
      climbSpeed = -0.6;



    // if(c.getLeftBumperPressed()) // decrement flywheel speed when left bumper pressed.
      // lock.set(Value.kForward);



    flyWheelMotor.set(flyWheelSpeeds[flyWheelMode]);
    intakeMotor.set(intakeSpeed);
    climbMotor.set(climbSpeed);
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
