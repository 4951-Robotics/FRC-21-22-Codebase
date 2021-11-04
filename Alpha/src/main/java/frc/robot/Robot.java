// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// // import edu.wpi.first.wpilibj.Joystick;
// import edu.wpi.first.wpilibj.PWMVictorSPX;
// import edu.wpi.first.wpilibj.Timer;
// import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;

import java.util.DoubleSummaryStatistics;

import edu.wpi.first.wpilibj.AnalogGyro;

import edu.wpi.first.wpilibj.Encoder;




/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */


  // NPM ports 2,3 are used for controlling motors
  private DifferentialDrive drive = new DifferentialDrive(new PWMVictorSPX(3), new PWMVictorSPX(2));
  // private DifferentialDrive drive = new DifferentialDrive(new PWMVictorSPX(0), new PWMVictorSPX(1));

  private final Timer timer = new Timer();
  private final Joystick stick = new Joystick(0);
  private final Encoder encoder = new Encoder(0, 1);
  
  private AnalogGyro gyro = new AnalogGyro(0);

  


  @Override
  public void robotInit() {
    

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // System.out.println(encoder.getDistance());
    System.out.println("angle is " + gyro.getAngle() + "\tt=" + timer.get());
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */


  @Override
  public void autonomousInit() {


    timer.reset();
    timer.start();
  }

  private void driveDumb(){
    if (timer.get() < 10){
      drive.tankDrive(0.8, 0.8);
    } else{
      drive.stopMotor();
    }
  }

  private void driveStraight(){
    double time = timer.get();
    if (time < 5){
      drive.stopMotor();
      
    }else if(time < 40){
      double error = 5; 


      double angle = gyro.getAngle();  
      if (angle < -error){
        // turn left
        drive.tankDrive(0.55, 0.75);
        System.out.println("turning left to correct");


      }else if (angle > +error){
        // turn right
        drive.tankDrive(0.75, 0.55);
        System.out.println("turning right to correct");

      }

      drive.tankDrive(0.6, 0.6);
    }else{
      drive.stopMotor();
    }
  }

  private void followAngle(){
    // uses pid to keep robot on an angle

    double time = timer.get();
    if (time < 5){
      drive.stopMotor();
      
    }else if(time < 40){
      double angle = gyro.getAngle();
      double target = 90;
      double tolerance = 5; // 5 degrees error allowed
      double error = target-angle;

      // p control
      double p_const = 0.8;
      double p_val = error/180.0;


      


    }else{
      drive.stopMotor();
    }
  }

  private void robotSpin(){
    double time = timer.get();
    if (time < 15){
      drive.tankDrive(0.6,-0.6);

    }else{
      drive.stopMotor();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override   
  public void autonomousPeriodic() {
    // followAngle();
    // robotSpin();
    driveDumb();
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}   
  

  /** This function is called periodically during operator control. */
  double forwardSpeed = 0;
  @Override
  public void teleopPeriodic() {
    double turnSpeed = stick.getX();
    

    double targetSpeed = stick.getY();
    forwardSpeed = (targetSpeed + forwardSpeed*30.0)/31.0;
    if (Math.abs(forwardSpeed) > 0.4)
    drive.arcadeDrive(forwardSpeed, 0.5*turnSpeed);
    
  

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
