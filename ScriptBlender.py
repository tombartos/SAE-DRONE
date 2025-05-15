import bpy
import bmesh
import mathutils

# This script is designed to be run in Blender's scripting environment.
# It assumes you have a mesh object selected in edit mode.
# It retrieves the selected face(s) and calculates their normals in world coordinates and the center relative to the
# center of the shape.
# Make sure you're in edit mode

# Assume the object you're editing is a thruster
thruster_obj = bpy.context.object
drone_origin = mathutils.Vector((0, 0, 0))  # All origins are now here

# Get mesh data in edit mode
bm = bmesh.from_edit_mesh(thruster_obj.data)
bm.normal_update()

# Get selected face(s)
selected_faces = [f for f in bm.faces if f.select]
if not selected_faces:
    print("No face selected!")
else:
    for f in selected_faces:
        # Face normal (local)
        local_normal = f.normal.copy()
        # Face center (local)
        local_center = f.calc_center_median()

        # Convert to world space
        world_normal = thruster_obj.matrix_world.to_3x3() @ local_normal
        world_center = thruster_obj.matrix_world @ local_center

        # Position relative to drone origin
        relative_position = world_center - drone_origin

        print(f"--- {thruster_obj.name} ---")
        print(f"Face center (relative to drone): {relative_position}")
        print(f"Direction vector (world normal): {world_normal}")
