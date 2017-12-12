package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelPedestrianWriterSource;

public class CsvPlaybackWriterSource extends ModelPedestrianWriterSource<CsvPlaybackOperational, CsvPlaybackPedestrianExtensions> {

	@Override
	protected boolean canWrite(CsvPlaybackPedestrianExtensions currentPedestrianExtension) {

		if(currentPedestrianExtension.isFirstDataSet()) {
			
			currentPedestrianExtension.setFirstDataSet(false);
			
			return false;
		}
		
		return true;
	}

	@Override
	protected String getPedestrianData(CsvPlaybackPedestrianExtensions currentPedestrianExtension, String format, String dataElement) {
	
		String dataText = null;
		StringBuilder builder = new StringBuilder();
				
		switch(dataElement) {
		
		case "distancesToPercepts":
			currentPedestrianExtension.getPerceptItems().forEach(item -> {
				builder.append(String.format(format, item.getDistanceToPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "anglesToPercepts":
			currentPedestrianExtension.getPerceptItems().forEach(item -> {
				builder.append(String.format(format, item.getAngleToPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "velocityMagnitudesOfPercepts":
			currentPedestrianExtension.getPerceptItems().forEach(item -> {
				builder.append(String.format(format, item.getVelocityMagnitudeOfPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "velocityAngleDifferencesToPercepts":
			currentPedestrianExtension.getPerceptItems().forEach(item -> {
				builder.append(String.format(format, item.getVelocityAngleDifferenceToPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "typesOfPercepts":
			currentPedestrianExtension.getPerceptItems().forEach(item -> {
				builder.append(String.format(format, item.getTypeOfPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "angleToGoal":
			dataText = String.format(format,currentPedestrianExtension.getAngleToGoal());
			break;
			
		case "lastVelocityMagnitude": 
			dataText = String.format(format,currentPedestrianExtension.getLastVelocityMagnitude());		
			break;
			
		case "lastVelocityAngleChange":
			dataText = String.format(format,currentPedestrianExtension.getLastVelocityAngleChange());
			break;
			
		case "velocityMagnitude":
			dataText = String.format(format,currentPedestrianExtension.getVelocityMagnitude());
			break;
			
		case "velocityAngleChange":
			dataText = String.format(format,currentPedestrianExtension.getVelocityAngleChange());
			break;
			
//		case "perceptionDistance":
//			
//			currentPedestrianExtension.getPerceptionDistanceSpace().forEach(distance -> {
//				builder.append(String.format(format,distance));
//				builder.append("|");
//			});
//			
//			dataText = builder.toString().substring(0, builder.length() - 1);
//			break;
//			
//		case "perceptionVelocityX":
//			currentPedestrianExtension.getPerceptionVelocityXSpace().forEach(velocityX -> {
//				builder.append(String.format(format,velocityX));
//				builder.append("|");
//			});
//		
//		dataText = builder.toString().substring(0, builder.length() - 1);
//			break;
//
//		case "perceptionVelocityY":
//			currentPedestrianExtension.getPerceptionVelocityYSpace().forEach(velocityY -> {
//				builder.append(String.format(format,velocityY));
//				builder.append("|");
//			});
//	
//			dataText = builder.toString().substring(0, builder.length() - 1);
//			break;
//
//		case "perceptionType":
//			currentPedestrianExtension.getPerceptionTypeSpace().forEach(type -> {
//				builder.append(String.format(format,type));
//				builder.append("|");
//			});
//	
//			dataText = builder.toString().substring(0, builder.length() - 1);
//			break;
//
//		case "pedestrianVelocityX":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityX());
//			break;
//			
//		case "pedestrianVelocityY":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityY());
//			break;
//			
//		case "pedestrianVelocityXLast":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityXLast());
//			break;
//			
//		case "pedestrianVelocityYLast":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityYLast());
//			break;
//			
//		case "pedestrianVelocityXLastSec":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityXLastSec());
//			break;
//			
//		case "pedestrianVelocityYLastSec":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityYLastSec());
//			break;
//			
//		case "pedestrianWalkingGoalX":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianWalkingGoalX());
//			break;
//			
//		case "pedestrianWalkingGoalY":
//			dataText = String.format(format,currentPedestrianExtension.getPedestrianWalkingGoalY());
//			break;
		}
		
		return dataText;
	}
}
