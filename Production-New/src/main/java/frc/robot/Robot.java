package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;
import edu.wpi.first.wpilibj.Ultrasonic;

import java.lang.System.Logger;
import java.security.cert.TrustAnchor;

import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

// https://software-metadata.revrobotics.com/REVLib.json
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;





public class Robot extends TimedRobot {

  //Motors
  // TalonSRX talonLeft = new TalonSRX(5);
  // VictorSPX victorLeft = new VictorSPX(4);

  // TalonSRX talonRight = new TalonSRX(3);
  // VictorSPX victorRight = new VictorSPX(6);

  // Camera


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
  DoubleSolenoid boost = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 7);
  DoubleSolenoid intakeFold = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1, 6);
  DoubleSolenoid climbLock = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 5);



  // DoubleSolenoid floats = new DoubleSolenoid(4,3);
  // DoubleSolenoid shield1 = new DoubleSolenoid(6,1);
  // DoubleSolenoid shield2 = new DoubleSolenoid(0,7);

  //data
  private final Timer timer = new Timer();
  private ADXRS450_Gyro gyro = new ADXRS450_Gyro();
  // private double angle = 0;

  //public boolean climbUp = false;
  //control
  private final XboxController c1 = new XboxController(0);
  private final XboxController c2 = new XboxController(1);
  public double intakeSpeed = 0.0;
  public double climbSpeed = 0.0;
  public boolean lockState = false;
  public double feederSpeed = 0.0;
  int flyWheelMode = 0;
  Value intakeFoldState = Value.kReverse;

  public boolean lightOn = false;
  public double lightValue = 0.0;
  
  AnalogInput ultrasonic = new AnalogInput(0);
  private static final double vtd = 1/0.023 ;
  // 24 inches ~ 0.57v ~0.024
  // 36 inches ~ 0.86v ~0.0239
  // 48 inches ~ 1.02v ~0.0212

  // 0.023 v per inch

  // stats toggle the smartdashboard
  boolean stats = true;

  //LED init
  Spark led = new Spark(0);


 //initalize trigger buttons

  // Joystick stick = 
 
//  final JoystickButton l2 = new JoystickButton(c, 9);
//  final JoystickButton r2 = new JoystickButton(c, 10);


  // final Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public void robotInit() {
    // comp.setClosedLoopControl(false);
    // arm.set(Value.kForward);
    
    // lock.set(Value.kReverse);
    //gyro.calibrate();

    System.out.println("go in");
    boost.set(Value.kForward);
    climbLock.set(Value.kReverse);
    intakeFold.set(Value.kReverse);

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

    //LED and Cam
    UsbCamera frontCam = CameraServer.startAutomaticCapture(0);
    UsbCamera intakeCamera = CameraServer.startAutomaticCapture(1);
    intakeCamera.setResolution(283,160);

    led.set(0.99);
  
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
    climbLock.set(Value.kReverse);

  }

  private void scoreTaxi(){
    // score 2 points by driving off the starting tarmac
    if (timer.get() < 1.5){
      drive.tankDrive(0.6, 0.6);
    }else{
      drive.stopMotor();
    }
  }

  /* Not Used?
  private void shoot(){
   if (timer.get() < 5){
      flyWheelMotor.set(0.3);
    }else{
      //ultrasonic.get() > tor.stopMotor();
    }
  }
  */

  public void scoreHigh(){
    climbLock.set(Value.kReverse);
    //fiddle with distance
    double t = timer.get();
    if(t < 0.4){
      // drive back
      drive.tankDrive(0.6, 0.6);
    }else if (1 < t && t < 2){
      drive.stopMotor();
      flyWheelMotor.set(-0.73);
    }else if (2 < t && t < 4){
      feederMotor.set(-0.3);
    }else if (4 < t && t < 5){
      flyWheelMotor.stopMotor();
      feederMotor.stopMotor();
    }else if (5 < t && t < 6){
      drive.tankDrive(0.6, 0.6);
    }else{
      drive.stopMotor();
    }
  }

  public void scoreHighULTRASONIC(){
    //fiddle with distance
    while (ultrasonic.getVoltage()*vtd > 1){
      // drive back
      drive.tankDrive(0.6, 0.6);
    }
    double t = timer.get();
    drive.stopMotor();
    if (1 < t && t < 2){
      flyWheelMotor.set(0.80);
    }else if (2 < t && t < 4){
      feederMotor.set(-0.3);
    }else{
      flyWheelMotor.stopMotor();
      feederMotor.stopMotor();
      drive.stopMotor();
    }

  }

  @Override
  public void autonomousPeriodic() {
    // scoreTaxi();
    scoreHigh();
  }

  @Override
  public void teleopInit() {
    climbLock.set(Value.kReverse);
    led.set(-0.99);

    // gyro.reset();
  }   


  // double aTarget = 0, bTarget = 0; // a for a button, b for bumpers"
  boolean compressing = false;


  @Override
  public void teleopPeriodic() {

    feederSpeed = 0.0;
    climbSpeed = 0.0;
    intakeSpeed = 0.0;
    intakeFoldState = Value.kReverse;
    flyWheelMode = 0;


    // DRIVING SYSTEM
    double forwardSpeed = c1.getLeftY() + c1.getRightY()*0.5;
    double turnSpeed = c1.getLeftX() + c1.getRightX()*0.5;

    if (forwardSpeed > 1)
      forwardSpeed = 1;

    if (turnSpeed > 1)
      turnSpeed = 1;


    

    // DO NOT TOUCH
    drive.arcadeDrive(forwardSpeed, -turnSpeed, true); // DO NOT TOUCH
    // DO NOT TOUCH ^


    // if(c1.getBButtonPressed()) // B for boost
    //   boost.toggle();


    // INTAKE SYSTEM |   CONTROLLER 2 right trigger sucks balls, left trigger spits balls

  
    double rightTrigger = c2.getRightTriggerAxis();
    double leftTrigger = c2.getLeftTriggerAxis();

    if(rightTrigger > 0){
      intakeSpeed = rightTrigger;
      feederSpeed = -0.45;
    }else if (leftTrigger > 0){
      intakeSpeed = -leftTrigger;
    }

    if(c2.getLeftTriggerAxis() > 0){
      intakeFoldState = Value.kForward; 
    }
    
    // FEEDER SYSTEM |   CONTROLLER 2 triangle(y) & X(a) hold down to spin
    if (c2.getAButton()){
      // activiate right trigger
      feederSpeed = -0.3;
    }else if (c2.getYButton()){
      feederSpeed = 0.4;
    }

/*
    //why can they change light value tho?
    if(c1.getBButton())
    {
      lightOn = false;
      lightValue = 0.99;
    }

    if(c1.getAButton())
    {
      lightValue = 0.77;
      lightOn = true;
    }

    if(c1.getYButton())
    {
      lightValue = -0.99;
    }
    led.set(lightValue);

*/

    // FLYWHEEL SYSTEM |  CONTROLLER 2 controlled by bumpers, RIGHT BUMPER increment speed, capped at highest speed, LEFT BUMPER reset speed to 0

    
    // double[] flyWheelSpeeds = {0.0, 0.5, 0.75}; // speeds when driving around, low goal, and high goal
    // // controls flywheel speed, lower speed is 10% speed, max speed is 100% speed
    // if(c2.getRightBumperPressed() && flyWheelMode < 2) // increment flywhell speed when right bumper pressed
    //   flyWheelMode++;
    // if(c2.getLeftBumperPressed())
    //   flyWheelMode = 0;

    double[] flyWheelSpeeds = {0.0, 0.45, 0.95}; // speeds when driving around, low goal, and high goal
    // controls flywheel speed, lower speed is 10% speed, max speed is 100% speed
    if(c2.getRightBumper()) // increment flywhell speed when right bumper pressed
      flyWheelMode = 2;
    if(c2.getLeftBumper())
      flyWheelMode = 1;
    
    
    
    // CLIMBER SYSTEM |   CONTROLLER 1 controlled by Y & A, press 
    
    if(lockState == false)
    {
      if(c1.getRightTriggerAxis() > 0) // up
        climbSpeed = 0.8;
      else if(c1.getLeftTriggerAxis() > 0) // down
        climbSpeed = -0.6;
    }

    if (c1.getXButtonPressed())
    {
      lockState = !lockState;
      climbLock.toggle();
    }

    //gyro.reset();
    //deletable(I think), just resets current direction to 0 degrees
    //Turns 180 with Controller 1 B button
    /*
    if(c1.getBButtonPressed()){
      final double time = timer.get();//start time(button pressed)
      double cur = timer.get();//current time
      final double period = 0.5;//max turn time
      final double An = gyro.getAngle();
      while(cur-period!=time){
        double curAn = gyro.getAngle();
        //use gyro
        if(curAn-180!=An||curAn+180!=An){
          drive.tankDrive(1, 1);
        }
      }
    }
    */
    double ultrasonicDist = ultrasonic.getVoltage()*vtd;
    
    
    if(50 <= ultrasonicDist && ultrasonicDist <= 62){
      //LIGHTS ARE GREEN
      System.out.println("in range");
      lightValue = 0.99;
    }else if(42 <= ultrasonicDist&&ultrasonicDist<50){ // 8 up 8 down range, for when we are apporaching shooting range
      // set to flashing orange
      lightValue = 0.65;
    }else if(63<ultrasonicDist&&ultrasonicDist <= 70){
      lightValue = 0.25;
    }else if(1000 <= ultrasonicDist && ultrasonicDist <= -1){ // low goal zone
      // to be implemented
      lightValue = 0.57;
    }else{
      lightValue = -0.99;
      // System.out.println("NOT IN RANGE NOT IN RANGE");
    }
    led.set(lightValue);

    // if(c.getLeftBumperPressed()) // decrement flywheel speed when left bumper pressed.
      // lock.set(Value.kForward);

    if (stats){
      SmartDashboard.putNumber("Flywheel Encoder", flyWheelEncoder.getVelocity());
      SmartDashboard.putNumber("Ultrasonic Distance", ultrasonicDist);
      SmartDashboard.putBoolean("Climb Lock Activated", lockState);
      SmartDashboard.putNumber("Ultrasonic Voltage", ultrasonic.getVoltage());
      SmartDashboard.putNumber("Ultrasonic Distance", ultrasonic.getVoltage()*vtd);
    }


    intakeMotor.set(intakeSpeed); // PWM 
    feederMotor.set(feederSpeed); // CAN
    flyWheelMotor.set(-flyWheelSpeeds[flyWheelMode]); //CAN
    intakeMotor.set(intakeSpeed); //PWM
    climbMotor.set(climbSpeed); //PWM

    intakeFold.set(intakeFoldState); // pneumatics


    
    System.out.println(lightValue);
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

