package arturpopov.basicprojectopengles;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Vector;

/**
 * Created by arturpopov on 09/02/2017.
 */

public class ObjectLoader
{
    static ArrayList<ArrayList<Float>> loadObjFile(String fileName, Context context)
    {
        if (context == null)
        {
            Log.d(LogTag.CONTEXT, "Context null");
            throw new RuntimeException("Context Null");
        }

        int tangentIndex = 0;
        int objectIndex = 0;

        ArrayList<ArrayList<Float>> resultList = new ArrayList<>();
        ArrayList<Float> vectorDataTemp = new ArrayList<>();
        ArrayList<Float> textureCoordTemp = new ArrayList<>();
        ArrayList<Float> normalDataTemp = new ArrayList<>();

        ArrayList<Float> vectorDataFinal = new ArrayList<>();
        ArrayList<Float> textureCoordFinal = new ArrayList<>();
        ArrayList<Float> normalDataFinal = new ArrayList<>();
        ArrayList<Float> tangentsDataFinal = new ArrayList<>();
        ArrayList<Float> biTangentsDataFinal = new ArrayList<>();

        String everything = FileReader.readFile("objFiles/" + fileName, context);
        String[] splitEverything = everything.split("\n");

        for (String line : splitEverything)
        {

            if (Objects.equals(line.substring(0, 2), "v "))
            {
                String stringBuffer = line.substring(2);
                String[] split = stringBuffer.split("\\s+");
                Float[] v = new Float[3];
                v[0] = Float.parseFloat(split[0]);
                v[1] = Float.parseFloat(split[1]);
                v[2] = Float.parseFloat(split[2]);
                Collections.addAll(vectorDataTemp, v);
            } else if (Objects.equals(line.substring(0, 2), "vt"))
            {
                String stringBuffer = line.substring(3);
                String[] split = stringBuffer.split("\\s+");
                Float[] textureCoordsArray = new Float[2];
                textureCoordsArray[0] = Float.parseFloat(split[0]);
                textureCoordsArray[1] = Float.parseFloat(split[1]);
                Collections.addAll(textureCoordTemp, textureCoordsArray);
            } else if (Objects.equals(line.substring(0, 2), "vn"))
            {
                String stringBuffer = line.substring(3);
                String[] split = stringBuffer.split("\\s+");
                Float[] normalArray = new Float[3];
                normalArray[0] = Float.parseFloat(split[0]);
                normalArray[1] = Float.parseFloat(split[1]);
                normalArray[2] = Float.parseFloat(split[2]);
                Collections.addAll(normalDataTemp, normalArray);
            } else if (Objects.equals(line.substring(0, 2), "f "))
            {
                String stringBuffer = line.substring(2);
                stringBuffer.replaceAll("\\//", " ");

                String[] firstSplit = stringBuffer.split("\\s+");
                String[] finalSplit = new String[9];
                int index = 0;
                for (String s : firstSplit)
                {
                    String[] split = s.split("/");
                    finalSplit[index++] = split[0];
                    finalSplit[index++] = split[1];
                    finalSplit[index++] = split[2];
                }
                Integer[] faceValues = new Integer[9];
                for (int i = 0; i < faceValues.length; i++)
                {
                    faceValues[i] = Integer.parseInt(finalSplit[i]) - 1;
                }
                vectorDataFinal.add(vectorDataTemp.get(faceValues[0]));
                textureCoordFinal.add(textureCoordTemp.get(faceValues[1]));
                normalDataFinal.add(normalDataTemp.get(faceValues[2]));
                vectorDataFinal.add(vectorDataTemp.get(faceValues[3]));
                textureCoordFinal.add(textureCoordTemp.get(faceValues[4]));
                normalDataFinal.add(normalDataTemp.get(faceValues[5]));
                vectorDataFinal.add(vectorDataTemp.get(faceValues[6]));
                textureCoordFinal.add(textureCoordTemp.get(faceValues[7]));
                normalDataFinal.add(normalDataTemp.get(faceValues[8]));
                tangentIndex += 3;
            } else if (Objects.equals(line.charAt(0), 'o') || line.isEmpty())
            {
                //Computing the tangents and bitangents
                //	opengl - tutorial.org
                for (int i = objectIndex; i < tangentIndex - 1; i += 3)
                {
                    Float[] v0 = {vectorDataFinal.get((i * 3) + 0), vectorDataFinal.get((i * 3) + 1), vectorDataFinal.get((i * 3) + 2)};
                    Float[] v1 = {vectorDataFinal.get(((i + 1) * 3) + 0), vectorDataFinal.get(((i + 1) * 3) + 1), vectorDataFinal.get(((i + 1) * 3) + 2)};
                    Float[] v2 = {vectorDataFinal.get(((i + 2) * 3) + 0), vectorDataFinal.get(((i + 2) * 3) + 1), vectorDataFinal.get(((i + 2) * 3) + 2)};

                    Float[] uv0 = {textureCoordFinal.get((i * 3) + 0), textureCoordFinal.get((i * 3) + 1), textureCoordFinal.get((i * 3) + 2)};
                    Float[] uv1 = {textureCoordFinal.get(((i + 1) * 3) + 0), textureCoordFinal.get(((i + 1) * 3) + 1), textureCoordFinal.get(((i + 1) * 3) + 2)};
                    Float[] uv2 = {textureCoordFinal.get(((i + 2) * 3) + 0), textureCoordFinal.get(((i + 2) * 3) + 1), textureCoordFinal.get(((i + 2) * 3) + 2)};

                    Float[] deltaPos1 = {v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
                    Float[] deltaPos2 = {v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};

                    Float[] deltaUV1 = {uv1[0] - uv0[0], uv1[1] - uv0[1], uv1[2] - uv0[2]};
                    Float[] deltaUV2 = {uv2[0] - uv0[0], uv2[1] - uv0[1], uv2[2] - uv0[2]};

                    float r = 1.0f / (deltaUV1[0] * deltaUV2[1] - deltaUV1[1] * deltaUV2[0]);
                    Float[] tangent = {
                            (deltaPos1[0] * deltaUV2[1] - deltaPos2[0] * deltaUV1[1]) * r,
                            (deltaPos1[1] * deltaUV2[1] - deltaPos2[1] * deltaUV1[1]) * r,
                            (deltaPos1[2] * deltaUV2[1] - deltaPos2[2] * deltaUV1[1]) * r,
                    };
                    Float[] biTangent = {
                            (deltaPos2[0] * deltaUV1[0] - deltaPos1[0] * deltaUV2[0]) * r,
                            (deltaPos2[1] * deltaUV1[0] - deltaPos1[1] * deltaUV2[0]) * r,
                            (deltaPos2[2] * deltaUV1[0] - deltaPos1[2] * deltaUV2[0]) * r,
                    };

                    Collections.addAll(tangentsDataFinal, tangent);
                    Collections.addAll(tangentsDataFinal, tangent);
                    Collections.addAll(tangentsDataFinal, tangent);

                    Collections.addAll(biTangentsDataFinal, biTangent);
                    Collections.addAll(biTangentsDataFinal, biTangent);
                    Collections.addAll(biTangentsDataFinal, biTangent);
                }
                if (line.isEmpty())
                {
                    break;

                }

            }
        }
        resultList.add(vectorDataFinal);
        resultList.add(textureCoordFinal);
        resultList.add(normalDataFinal);
        resultList.add(tangentsDataFinal);
        resultList.add(biTangentsDataFinal);
        return resultList;
    }
}
