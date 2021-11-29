package frc.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
    // 3,2 robot when specified positive numbers, will move in 
    // the direction of the arrows
    private DifferentialDrive drive = new DifferentialDrive(
        new PWMVictorSPX(3), new PWMVictorSPX(2));

    // directions defined in the POV:
    // imagine yourself behind the the robot, and the robot has its
    // back facing you (if the robot was to drive forward, it would 
    // drive away from you)
    private Encoder leftEncoder = new Encoder(0, 1);


    private AnalogGyro gyro = new AnalogGyro(0);

    public DriveTrain(){
        

    }

    public void logSmartDashboard(){
        SmartDashboard.putNumber("Left Encoder Dist", this.leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Encoder Dist", this.rightEncoder.getDistance());
		SmartDashboard.putNumber("Left Speed", this.leftEncoder.getRate());
		SmartDashboard.putNumber("Right Speed", this.rightEncoder.getRate());
		SmartDashboard.putNumber("Gyro", this.gyro.getAngle());
    }
}
