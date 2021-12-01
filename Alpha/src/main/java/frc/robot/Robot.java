// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.PWMTalonSRX;

import java.util.DoubleSummaryStatistics;

import javax.swing.plaf.TreeUI;

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


   //victorsp 0, 3 not working
   //PWM 0, 3 not working\
   //Talon 0, 3 not working

  // NPM ports 2,3 are used for controlling motors
  //private DifferentialDrive drive = new DifferentialDrive(new PWMTalonSRX(0), new PWMTalonSRX(3));
  private DifferentialDrive drive = new DifferentialDrive(new PWMVictorSPX(3), new PWMVictorSPX(2));

  private final Timer timer = new Timer();
  private final Joystick stick = new Joystick(0);
  // private final Encoder encoder = new Encoder(0, 1);
  
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
    // System.out.println("angle is " + gyro.getAngle() + "\tt=" + timer.get());
    // System.out.println(gyro.getAngle() + ",");
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
      drive.tankDrive(0.5, 0.5);
    } else{
      drive.stopMotor();
    }
  }

  private void driveStraight(){
    double time = timer.get();
    if (time < 1){
      drive.stopMotor();
      
    }else if(time < 40){
      double power = 0.7;
      double turn_power = -0.08 * gyro.getAngle();


      drive.arcadeDrive(power, turn_power, true);
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
      double P = 0.5; double I = 0.5; double D = 0.5;

      double angle = gyro.getAngle();
      double target = 90;
      double error =  target-angle;
      
      // double integral += (error*.02); // Integral is increased by the error*time (which is .02 seconds using normal IterativeRobot)
      // double derivative = (error - ) / .02;
      
      // double motors = P*error + I*this.integral + D*derivative;

      


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

  private void driveTest(){
    double time = timer.get();
    if (time < 1){
      drive.stopMotor();
      
    }else if(time < 40){
      double speed = 0.6;
      double allowed_error = 3.0;
      double angle = gyro.getAngle();

      double turn_power = 0;

      if(angle > allowed_error || angle < -allowed_error){
        // System.out.println("ok outside range, setting to not 0");
        turn_power = -0.15 * angle;
      }

      // drive.arcadeDrive(speed, turn_power, true);
    }else{
      drive.stopMotor();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() { // negative turns left, -angle = left
    // followAngle();
    // robotSpin();
    driveDumb();
    // driveStraight();
    // driveTest();
  }


  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}   
  

  /** This function is called periodically during operator control. */
  double forwardSpeed = 0;
  boolean mode = false;
  @Override
  public void teleopPeriodic() {
    //System.out.println("manual control");


    double turnSpeed = stick.getX();
    double targetSpeed = stick.getY();
  
    System.out.println(mode);
    if(stick.getRawButton(2)) //B on controller
    {
      mode = true;
    }
    if(stick.getRawButton(2) && mode == true)
    {
      mode = false;
    }

    if(mode)
    {
      drive.arcadeDrive(2*targetSpeed, 0.5*turnSpeed);
    }

    if(!mode)
    {
      drive.arcadeDrive(targetSpeed, 0.5*turnSpeed);

    }
    
    // forwardSpeed = (targetSpeed + forwardSpeed*30.0)/31.0;
    // if (Math.abs(forwardSpeed) > 0.4)
    // drive.arcadeDrive(forwardSpeed, 0.5*turnSpeed);
    
  

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {

    // try {
    //   //  

    //   FileWriter logwriter = new FileWriter("testfile.txt");
    //   logwriter.append(timer.get() + " " + gyro.getAngle());
    //   logwriter.close();


    // } catch (IOException e){
    //   System.out.println("problem");
    //   e.printStackTrace();
    // }

    // try{
    //   BufferedWriter writer = new BufferedWriter(
    //     new FileWriter("C:\\Users\\Henry\\RobotRepo\\Alpha\\src\\main\\java\\frc\\robot\\Robot.java\\testfile.txt"));
    //   writer.write(timer.get() + " " + gyro.getAngle() + "\n");
    //   writer.close();
    // } catch (IOException e){
    //   System.out.println("PROBLEM PROBLEM NOT GOOD");
    //   e.printStackTrace();
    // }
    
    // System.out.println(gyro.getAngle());
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
