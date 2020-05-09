package engine.terrains;

import engine.models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import engine.loaders.Loader;
import engine.utils.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Terrain {

    private static final float SIZE = 800;
    private static final int MAX_HEIGHT = 40;
    private static final int MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;

    public float[][] heights;

    private int VERTEX_COUNT;
    private float[] vertices;
    private float[] normals;
    private float[] textureCoords;
    private int[] indices;

    private int verticesVboId;
    private int textureCoordsVboId;
    private int normalsVboId;
    private int indicesVboId;

    public Terrain(int gridX, int gridZ, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightmap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(heightmap);
    }

    public Vector3f worldToTerrainVector(Vector3f wv) {
        return new Vector3f(wv.x * (VERTEX_COUNT/SIZE), wv.y * (VERTEX_COUNT/SIZE), wv.z * (VERTEX_COUNT/SIZE));
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX < 0 || gridX >= heights.length - 1 || gridZ < 0 || gridZ >= heights.length - 1 ) {
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        if (xCoord <= (1 - zCoord)) {
            return Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            return Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
    }

    public void updateTerrain() {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        vertices = new float[count * 3];
        normals = new float[count * 3];
        textureCoords = new float[count * 2];
        indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int z = 0; z < VERTEX_COUNT; z++) {
            for (int x = 0; x < VERTEX_COUNT; x++) {
                vertices[vertexPointer * 3] = (float) x / ((float) VERTEX_COUNT - 1) * SIZE;
                float height = heights[x][z];
                heights[x][z] = height;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) z / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(x, z);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) x / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) z / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }


//        this.model = Loader.loadToVAO(getModel().getVaoId(), vertices, textureCoords, normals, indices);
        // decompiled loader
        //return Loader.loadToVAO(vertices, textureCoords, normals, indices);
        int vaoId = this.model.getVaoId();
        //bindIndicesBuffer(indices);
        /////////vaos.add(vaoId);
        GL30.glBindVertexArray(vaoId);

//        int indicesVboId = GL15.glGenBuffers();
        /////////vbos.add(indicesVboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);

        //IntBuffer buffer = storeDataInIntBuffer(indices);
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        //storeDataInAttributeList(0,3, vertices);
//        verticesVboId = GL15.glGenBuffers();
        /////////vbos.add(verticesVboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verticesVboId);

        //FloatBuffer buffer = storeDataInFloatBuffer(vertices);
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //storeDataInAttributeList(1,2, textureCoords);
//        textureCoordsVboId = GL15.glGenBuffers();
        /////////vbos.add(textureCordsVboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureCoordsVboId);

        //FloatBuffer buffer = storeDataInFloatBuffer(textureCoords);
        FloatBuffer textureCoordsBuffer = BufferUtils.createFloatBuffer(textureCoords.length);
        textureCoordsBuffer.put(textureCoords);
        textureCoordsBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureCoordsBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //storeDataInAttributeList(2,3, normals);
//        normalsVboId = GL15.glGenBuffers();
        /////////vbos.add(normalsVboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalsVboId);

        //FloatBuffer buffer = storeDataInFloatBuffer(data);
        FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normals.length);
        normalsBuffer.put(normals);
        normalsBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalsBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //unbindVAO();
        GL30.glBindVertexArray(0);

        this.model = new RawModel(vaoId, indices.length);
        //
    }

    private RawModel generateTerrain(String heightmap) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightmap + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        vertices = new float[count * 3];
        normals = new float[count * 3];
        textureCoords = new float[count * 2];
        indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int z = 0; z < VERTEX_COUNT; z++) {
            for (int x = 0; x < VERTEX_COUNT; x++) {
                vertices[vertexPointer * 3] = (float) x / ((float) VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(x, z, image);
                heights[x][z] = height;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) z / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(x, z, image);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) x / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) z / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        // decompiled loader
        //return Loader.loadToVAO(vertices, textureCoords, normals, indices);
        int vaoId = GL30.glGenVertexArrays();
        //bindIndicesBuffer(indices);
        /////////vaos.add(vaoId);
        GL30.glBindVertexArray(vaoId);

        indicesVboId = GL15.glGenBuffers();
        /////////vbos.add(indicesVboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);

        //IntBuffer buffer = storeDataInIntBuffer(indices);
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        //storeDataInAttributeList(0,3, vertices);
        verticesVboId = GL15.glGenBuffers();
        /////////vbos.add(verticesVboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verticesVboId);

        //FloatBuffer buffer = storeDataInFloatBuffer(vertices);
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //storeDataInAttributeList(1,2, textureCoords);
        textureCoordsVboId = GL15.glGenBuffers();
        /////////vbos.add(textureCordsVboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureCoordsVboId);

        //FloatBuffer buffer = storeDataInFloatBuffer(textureCoords);
        FloatBuffer textureCoordsBuffer = BufferUtils.createFloatBuffer(textureCoords.length);
        textureCoordsBuffer.put(textureCoords);
        textureCoordsBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureCoordsBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //storeDataInAttributeList(2,3, normals);
        normalsVboId = GL15.glGenBuffers();
        /////////vbos.add(normalsVboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalsVboId);

        //FloatBuffer buffer = storeDataInFloatBuffer(data);
        FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normals.length);
        normalsBuffer.put(normals);
        normalsBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalsBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //unbindVAO();
        GL30.glBindVertexArray(0);

        return new RawModel(vaoId, indices.length);
        //
    }

    public void moveTerrainHeight(int pointX, int pointZ, float value) {
        if ((pointX >= 0 && pointX < VERTEX_COUNT) && (pointZ >= 0 && pointZ < VERTEX_COUNT)) {
            heights[pointX][pointZ] += value;
        }
    }

    private Vector3f calculateNormal(int x, int z) {
        float heightL = getHeight(x - 1, z);
        float heightR = getHeight(x + 1, z);
        float heightD = getHeight(x, z - 1);
        float heightU = getHeight(x, z + 1);

        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightL = getHeight(x - 1, z, image);
        float heightR = getHeight(x + 1, z, image);
        float heightD = getHeight(x, z - 1, image);
        float heightU = getHeight(x, z + 1, image);

        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private float getHeight(int x, int z) {
        if (x < 0 || x >= heights.length || z < 0 || z >= heights[0].length) {
            return 0;
        }
        return heights[x][z];
    }

    private float getHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
            return 0;
        }

        float height = image.getRGB(x, z);
        height += MAX_PIXEL_COLOR / 2f;
        height /= MAX_PIXEL_COLOR / 2f;
        height *= MAX_HEIGHT;
        return height;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }
}
