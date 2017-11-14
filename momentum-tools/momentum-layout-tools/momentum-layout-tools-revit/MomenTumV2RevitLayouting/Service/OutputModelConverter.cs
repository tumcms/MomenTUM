using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MomenTumV2RevitLayouting.Model.Output;
using Autodesk.Revit.UI;

namespace MomenTumV2RevitLayouting.Service
{
    class OutputModelConverter
    {
        public Simulator ToSimulator(IList<Model.Scenario> allScenarios)
        {
            var simulator = new Simulator();

            simulator.Version = "2.0.2";
            simulator.Name = "RevitLayouting";
            simulator.Layout = new Layout();
            simulator.Layout.scenarios = ScenarioToOutputScenario(allScenarios);

            return simulator;
        }

        private Point Vector2DToPoint(Vector2D vector)
        {
            var point = new Point();

            point.X = vector.X;
            point.Y = vector.Y;

            return point;
        }

        private Point[] Polygons2DToPoints(Vector2D[] polygons)
        {
            var points = new List<Point>();

            var duplicateFreePolygons = RemoveDuplicates(polygons.ToList());
            var counterClockWiseOrdered = OrderPointsCounterClockWise(duplicateFreePolygons);

            foreach (var polygon in counterClockWiseOrdered)
            {
                points.Add(Vector2DToPoint(polygon));
            }

            return points.ToArray();
        }

        private Obstacle[] WallToObstacle(IList<Model.Segment2D> walls, string type)
        {
            var obstacles = new List<Obstacle>(walls.Count);

            for (int i = 0; i < walls.Count; i++)
            {
                var obstacle = new Obstacle();
                var wall = walls[i];

                obstacle.Id = i;
                obstacle.Name = type + i;
                obstacle.Type = type;
                obstacle.Points = Polygons2DToPoints(wall.GetVertices());

                obstacles.Add(obstacle);
            }

            return obstacles.ToArray();
        }
        private Scenario[] ScenarioToOutputScenario(IList<Model.Scenario> oldScenarios)
        {
            var scenarios = new List<Scenario>(oldScenarios.Count);
            var reportMessage = string.Empty;

            for (int i = 0; i < oldScenarios.Count; i++)
            {
                var scenario = new Scenario();
                var oldScenario = oldScenarios[i];

                scenario.Id = i;
                scenario.Name = oldScenario.Name;
                scenario.MaxX = oldScenario.MaxCoordinates[0];
                scenario.MaxY = oldScenario.MaxCoordinates[1];
                scenario.MinX = oldScenario.MinCoordinates[0];
                scenario.MinY = oldScenario.MinCoordinates[1];
                scenario.obstacles = WallToObstacle(oldScenario.WallList, "Wall");

                var areas = OriginOrDestinationToArea(oldScenario.OriginList, "Origin");
                areas.AddRange(OriginOrDestinationToArea(oldScenario.DestinationList, "Destination"));

                scenario.areas = areas.ToArray();

                scenarios.Add(scenario);
            }

            ShowReport(oldScenarios);

            return scenarios.ToArray();
        }

        private void ShowReport(IList<Model.Scenario> scenarios)
        {
            string reportMessage = "";

            for (int i = 0; i < scenarios.Count; i++)
            {
                var scenario = scenarios[i];
                reportMessage += "Scenario: " + scenario.Name +
                    "\nWalls: " + scenario.WallList.Count +
                    "\nSolids: " + scenario.SolidList.Count +
                    "\nOrigins: " + scenario.OriginList.Count +
                    "\nDestinations: " + scenario.DestinationList.Count +
                    "\n\n";
            }

            reportMessage += "Close this dialog to continue to saving...";

            TaskDialog.Show("Layouting Report", reportMessage);
        }

        private List<Area> OriginOrDestinationToArea(IList<Model.Polygon2D> originsOrDestinations, string type)
        {
            var areas = new List<Area>();

            for (int i = 0; i < originsOrDestinations.Count; i++)
            {
                var area = new Area();
                var polygon = originsOrDestinations[i];

                area.Id = i;
                area.Name = type + i;
                area.Type = type;
                area.Points = Polygons2DToPoints(polygon.Vertices);

                areas.Add(area);
            }

            return areas;
        }

        private List<Vector2D> RemoveDuplicates(List<Vector2D> polygons)
        {
            List<Vector2D> duplicateFreeVertices = new List<Vector2D>();

            foreach (var vertex in polygons)
            {
                bool mustAdd = true;
                foreach (var vert in duplicateFreeVertices)
                {
                    if (vertex.X == vert.X && vertex.Y == vert.Y)
                    {
                        mustAdd = false;
                    }
                }

                if (mustAdd == true)
                {
                    duplicateFreeVertices.Add(vertex);
                }
            }

            return duplicateFreeVertices;
        }

        private List<Vector2D> OrderPointsCounterClockWise(List<Vector2D> vectors)
        {
            Vector2D center = CalculateCenterPoint(vectors);

            vectors = vectors.OrderBy(vertex =>
                Math.Atan2(vertex.X - center.X,
                vertex.Y - center.Y)).ToList();

            vectors.Reverse();
            return vectors;
        }

        private Vector2D CalculateCenterPoint(List<Vector2D> vectors)
        {
            double x = 0.0, y = 0.0;

            foreach (var vector in vectors)
            {
                x += vector.X;
                y += vector.Y;
            }

            return new Vector2D(x / vectors.Count, y / vectors.Count);
        }
    }
}
