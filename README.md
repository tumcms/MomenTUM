<img src="https://www.cms.bgu.tum.de/images/forschung/MomenTUM/MomenTUM.png" width="300">

The Java software [MomenTUM](https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum) is the agent-based pedestrian simulation framework that was developed under the lead of [Dr. Peter M. Kielar](https://www.cms.bgu.tum.de/de/team/kielar) at the [Chair of Computational Modeling and Simulation](https://www.cms.bgu.tum.de/en/) at the [Technische Universität München]( https://www.tum.de/en/homepage/). MomenTUM is still under development and a simulation tool for researcher and practitioners in the context of pedestrian dynamics. The current version of MomenTUM is 2.0.2.

<p float="left"> <img src="https://www.cms.bgu.tum.de/images/forschung/MomenTUM/2017_MomenTUMv2_2DVisualisation.png" height="120"> <img src="https://www.cms.bgu.tum.de/images/forschung/MomenTUM/2017_MomenTUMv2_3DVisualisation.png" height="120"> <img src="https://www.cms.bgu.tum.de/images/forschung/MomenTUM/2017_MomenTUMv2_Density.png" height="120"> <img src="https://www.cms.bgu.tum.de/images/forschung/MomenTUM/MomenTUMv2_BTTW_Evacuation.png" height="120"></p>


## General Information
Agent-based pedestrian behavior simulators are computational systems that implement models and theories that describe the behavior of individual pedestrians. Ultimately, pedestrian simulations will provide a method to forecast pedestrian behavior for safety and economics purposes.

In order to develop a pedestrian simulator, multiple infrastructure and non-behavior concepts have to be implemented. Such components can be reused for the development of new simulation models if the simulator is designed as a modular simulation framework.

For pedestrian dynamics research, the chair of Chair of Computational Modeling and Simulation created a Java-based pedestrian simulation framework. The framework links modern concepts of practical and theoretical computer science with our in-depth background in pedestrian dynamics; thus, it is a perfect platform to implement new but also well-known behavior modeling approaches from the field of pedestrian dynamics.

The simulation framework enables our research group to rapidly research, develop, implement, analyze, and compare pedestrian behavior models. The system is a generic, extensible, and modular approach that integrates a broad range of pedestrian, utility and layout models. MomenTUM provides a flexible execution pipeline to run any number of behavior models in arbitrary combinations. For example, a cellular automata model can be connected to a graph based routing model that is connected to an origin-destination matrix approach.

The unique qualities of MomenTUM help to design and execute simulations without writing code and to add new theory implementations without changing the given infrastructure. Thus, MomenTUM is a simulation system that empowers researchers and practitioners by providing a flexible toolbox that adapts to the needs of its users. 

We recommend checking MomenTUM’s [technical report](http://www.cms.bgu.tum.de/publications/reports/2016_Kielar_MomenTUMv2.pdf) for further details on the framework.


## Project structure
MomenTUM is a modular software system that comprises a set of Maven based projects. Here, we give a short overview of the projects.

* configuration: The configuration project defines classes and types. A XML-based configuration file will be translated to elements of the configuration project to provide the configuration objects of a simulation.

* third-party: Some projects are not part of the Maven repository and are stored in the third-party project. Thus, you need to run Maven install on this project.

* utility: The utility project provides a set of mathematical and general functionally that are used in the framework and the implemented models. For example, graph theory, lattice methods, and geometry methods. We use multiple libraries in the utility project.

* data: The data project comprises agent and layout classes as well as the corresponding management and service classes.

* model: The model projects holds all implemented models. There are many different models for non-behavior and pedestrian behavior implemented. For example, pedestrian generator models, graph models, output writers, wayfinding models, walking models, etc.

* simulator: The simulator projects is the starting point for conduction simulations. It handles the control of the simulation generically. 

* visualization-2d: MomenTUM brings its 2D visualization tool. This is a standalone JavaFX application that reads an XML layout file and CSV output file (including an index file) for visualizing the simulation data. MomenTUM provides a Unity-3D visualization that is not on GitHub. Please contact us for further details.

* package: The simulation framework comprises two different executable projects: the simulator and the visualization. Both can be packaged into a runnable jar by their corresponding package projects.

* layout-tools: The layout-tools projects provide mechanisms to generate simulation scenarios based on different input data (AutoCAD, Revit, OSM). The C# AutoCAD plugin is here on GitHub. Make sure to get the libraries before starting (see the documentation in the project folder). The other tools are not on GitHub. Please contact us for further details.

* momentum-users: Here, all configuration files that were used in research and are open to the public are stored. We never guarantee that any of this files work with the current version of MomenTUM.

The documentation folder provides some basic information on how to use the framework. However, the documentation is still not complete.


## Getting started 
1. Clone the git repository to create the code-base.

2. Start your favorite IDE and import the Maven projects to your environment (we use Eclipse). 

3. Make sure you run Maven install on the third-party project because we use libraries that are not part of the Maven repositories.

4. Typically, the code compiles without errors and you can explore the code-base.

5. Run simulations (simulator project) via command line (or IDE) with the command line parameter: --config “full path to configuration XML file”

Furthermore, we use for Eclipse the e(fx)clipse JavaFX plugin, the M2Eclipse Maven plugin, and the JDK version 1.8.0_131.

The simulation framework uses a configuration file that defines the simulation to execute (adaptive object modeling and strategy pattern). We recommend reading the contents of the UserGuide in the documentation folder to understand the basic elements of the configuration.


## License
Permission is hereby granted, free of charge, to use and/or copy this software for non-commercial research and education purposes if the authors of this software and their research papers are properly cited. For citation information visit the [MomenTUM hompeage](https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum).


However, further rights are not granted.
If you need another license or specific rights, contact us!

The copyright notice Licence.txt  shall be included in all copies or substantial portions of the Software.

## Copyright notes
MomenTUM uses several software libraries. The corresponding licenses can be found in the third-party lib folders. Other libraries will provide their licenses via Maven.


## Contributors
Many researchers and students of the Technical University Munich contributed to the simulation framework:

Researcher:
* Peter M. Kielar
* Daniel H. Biedermann

Master thesis:
* Sven Lauterbach
* Daniel Büchele
* Benedikt Schwab

Interdisciplinary computer science project:
* Martin Sigl
* Christian Thieme

Bachelor thesis:
* Sonja Germscheid
* Benedikt Schwab

Student assistant:
* Bernd Tornede
* Quirin Aumann
* Fabian Hirt
* Carlos Osorio

## Contact
Mail to [Peter Kielar](mailto:peter.kielar@tum.de) and visit the homepage of the [Chair of Computational Modeling and Simulation](https://www.cms.bgu.tum.de/en/).

