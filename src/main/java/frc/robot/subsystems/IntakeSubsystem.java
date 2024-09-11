// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.Config;
import frc.robot.Constants.Intake;

public class IntakeSubsystem extends SubsystemBase {

  private final TalonFX intakeMotor;
  private final ShuffleboardTab tab = Shuffleboard.getTab("Intake");
  private final DigitalInput noteSensor;
  private IntakeMode intakeMode;
  private IntakeMode pastMode;
  private double timeSincePenaltyHazard;
  private boolean pastPenalty;

  public enum IntakeMode {
    INTAKE(Intake.Modes.INTAKE),
    HOLD(Intake.Modes.HOLD),
    REVERSE(Intake.Modes.REVERSE),
    SHOOT_SPEAKER(Intake.Modes.SHOOT_SPEAKER),
    SHOOT_AMP(Intake.Modes.SHOOT_AMP);

    public final IntakePowers modePowers;

    private IntakeMode(IntakePowers modePowers) {
      this.modePowers = modePowers;
    }
  }

  public record IntakePowers(double intakeSpeed, double serializerSpeed) {
    public IntakePowers(double intakeSpeed, double serializerSpeed) {
      this.intakeSpeed = intakeSpeed;
      this.serializerSpeed = serializerSpeed;
    }
  }

  /** Creates a new IntakeSubsystem. */
  public IntakeSubsystem() {

    intakeMotor = new TalonFX(Intake.Ports.INTAKE_MOTOR_PORT);
    noteSensor = new DigitalInput(Intake.Ports.INTAKE_SENSOR_PORT);
    intakeMotor.clearStickyFaults();

    intakeMotor.setNeutralMode(NeutralModeValue.Brake);
    intakeMotor.setInverted(true);

    intakeMode = IntakeMode.HOLD;

    timeSincePenaltyHazard = 7;

    if (Config.SHOW_SHUFFLEBOARD_DEBUG_DATA) {
      tab.addDouble("intake voltage", () -> intakeMotor.getMotorVoltage().getValueAsDouble());
      tab.addString("Current Mode", () -> intakeMode.toString());

    }
  }

  public void setIntakeMode(IntakeMode intakeMode) {
    this.intakeMode = intakeMode;
  }



  private IntakeMode getIntakeMode() {
    return intakeMode;
  }

  @Override
  public void periodic() {
    intakeMotor.set(intakeMode.modePowers.intakeSpeed);
  }
}
