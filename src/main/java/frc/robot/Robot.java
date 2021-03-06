/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;

/**
 * * This is the code for the T-Shirt Cannon. Works pretty good.
 * 
 * Basic info: Drives with logitech flight stick, air compressor with button 12,
 * fire with triger, thumb button, and button 5/6 for top and bottom array.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private SpeedControllerGroup leftSideSpeedControllerGroup;
  private SpeedControllerGroup rightSideSpeedControllerGroup;

  private Solenoid Solenoid_1;
  private Solenoid Solenoid_2;
  private Solenoid Solenoid_3;
  private Solenoid Solenoid_4;
  private Solenoid Solenoid_5;
  private Solenoid Solenoid_6;

  private static final int kJoystickChannel = 0;
  private static final int kGyroChannel = 0;
  private Joystick m_stick;
  private AnalogGyro m_gyro;

  private Solenoid[] topSolonoids;
  private Solenoid[] bottomSolonoids;

  private Compressor TestCompressor;

  @Override
  public void robotInit() {
    Solenoid_1 = new Solenoid(0);
    Solenoid_2 = new Solenoid(1);
    Solenoid_3 = new Solenoid(2);
    Solenoid_4 = new Solenoid(3);
    Solenoid_5 = new Solenoid(4);
    Solenoid_6 = new Solenoid(5);
    TestCompressor = new Compressor(0);
    TestCompressor.setClosedLoopControl(false);

    m_stick = new Joystick(kJoystickChannel);

    leftSideSpeedControllerGroup = new SpeedControllerGroup(new WPI_TalonSRX(1), new WPI_TalonSRX(2));
    rightSideSpeedControllerGroup = new SpeedControllerGroup(new WPI_TalonSRX(3), new WPI_TalonSRX(4));
    m_myRobot = new DifferentialDrive(leftSideSpeedControllerGroup, rightSideSpeedControllerGroup);

    topSolonoids = new Solenoid[] { Solenoid_1, Solenoid_2, Solenoid_3 };
    bottomSolonoids = new Solenoid[] { Solenoid_4, Solenoid_5, Solenoid_6 };
    m_gyro = new AnalogGyro(kGyroChannel);
    System.out.println("rgirhov");
  }

  @Override
  public void teleopPeriodic() {
    //? Only run the compressor if button 12 is held
    if (m_stick.getRawButton(12)){
      TestCompressor.setClosedLoopControl(true);
    } else {
      TestCompressor.setClosedLoopControl(false);
    }
    //// fireCannon(Solenoid_1, m_stick, 8);
    //// fireCannon(Solenoid_2, m_stick, 10);
    //// fireCannon(Solenoid_3, m_stick, 12);
    //// fireCannon(Solenoid_4, m_stick, 7);
    //// fireCannon(Solenoid_5, m_stick, 9);
    //// fireCannon(Solenoid_6, m_stick, 11);

    // Logic to control trigering is inside
    // TODO: Move the logic out here, makes more sense.
    fireTrio(topSolonoids, m_stick, 5);
    fireTrio(bottomSolonoids, m_stick, 6);

    // Make robit go
    double[] movementList = adjustJoystickInput(-m_stick.getY(), m_stick.getX(), m_stick.getThrottle());
    m_myRobot.arcadeDrive(movementList[0], movementList[1]);
    System.out.println(m_gyro.getAngle());
  }

  @Override
  public void testInit() {
    TestCompressor = new Compressor(0);
    TestCompressor.setClosedLoopControl(true);

  }

  @Override
  public void testPeriodic() {
    System.out.print(String.format("Compressor Status: {0}", TestCompressor.getPressureSwitchValue()));
    System.out.print(String.format("Compressor Status: {0}", TestCompressor.getCompressorCurrent()));
  }

  public void fireCannon(Solenoid solonoidToFire, Joystick joystick, int secondaryButton) {
    //! Obsolete, do not use. 
    if (joystick.getRawButton(1) && joystick.getRawButton(secondaryButton)) {
      solonoidToFire.set(true);
    } else {
      solonoidToFire.set(false);
    }
  }

  private void fireTrio(Solenoid[] solonoidsToFire, Joystick joystick, int secondaryButton) {
    // Should be called every update loop, checks for butttons and may fire
    if (joystick.getRawButton(2) && joystick.getRawButton(secondaryButton)) {
      solonoidsToFire[0].set(true);
      solonoidsToFire[1].set(true);
      solonoidsToFire[2].set(true);
    } else {
      solonoidsToFire[0].set(false);
      solonoidsToFire[1].set(false);
      solonoidsToFire[2].set(false);
    }
  }

  private double[] adjustJoystickInput(double yPower, double zPower, double Throttle) {
    double adjustedThrottle = Math.sqrt(((-1 * Throttle) + 1) / 2); // Seems like a pretty good throttle curve
    double yPowerOut = yPower * adjustedThrottle;
    double zPowerOut = zPower * adjustedThrottle * 0.85;

    double[] outputList = new double[] { yPowerOut, zPowerOut };
    return outputList;
  }
}
 