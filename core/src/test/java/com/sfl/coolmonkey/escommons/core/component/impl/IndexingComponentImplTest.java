package com.sfl.coolmonkey.escommons.core.component.impl;

import com.sfl.coolmonkey.escommons.core.component.ElasticsearchClientWrapper;
import com.sfl.coolmonkey.escommons.core.component.IndexNameGenerationComponent;
import com.sfl.coolmonkey.escommons.core.component.IndexingComponent;
import com.sfl.coolmonkey.escommons.core.component.MappingsComponent;
import com.sfl.coolmonkey.escommons.core.test.AbstractCoreUnitTest;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.elasticsearch.common.util.set.Sets;
import org.junit.Test;

import java.util.*;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;

/**
 * Created by Arthur Asatryan.
 * Date: 7/14/17
 * Time: 5:07 PM
 */
public class IndexingComponentImplTest extends AbstractCoreUnitTest {

    //region Test subject and mocks
    @TestSubject
    private IndexingComponent indexingComponent = new IndexingComponentImpl();

    @Mock
    private MappingsComponent mappingsComponent;

    @Mock
    private IndexNameGenerationComponent indexNameGenerationComponent;

    @Mock
    private ElasticsearchClientWrapper elasticsearchClientWrapper;
    //endregion

    //region Constructors
    public IndexingComponentImplTest() {
    }
    //endregion

    //region Test methods

    //region createIndexAndSetupMappings
    @Test
    public void testCreateIndexAndSetupMappingsWithInvalidArguments() {
        // Test data
        // Reset
        resetAll();
        // Expectations
        // Replay
        replayAll();
        // Run test scenario
        try {
            indexingComponent.createIndexAndSetupMappings(null, Collections.singletonList(UUID.randomUUID().toString()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            indexingComponent.createIndexAndSetupMappings(UUID.randomUUID().toString(), null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateIndexAndSetupMappings() {
        // Test data
        final String newIndexName = UUID.randomUUID().toString();
        final String originalIndex = UUID.randomUUID().toString();
        final String type = UUID.randomUUID().toString();
        final List<String> types = new ArrayList<>();
        final String mappings = "Some dummy mappings";
        types.add(type);
        // Reset
        resetAll();
        // Expectations
        expect(indexNameGenerationComponent.generateNameForGivenIndex(originalIndex)).andReturn(newIndexName);
        expect(elasticsearchClientWrapper.createIndex(newIndexName)).andReturn(true);
        expect(mappingsComponent.readMappings(originalIndex, type)).andReturn(mappings);
        expect(elasticsearchClientWrapper.putMapping(newIndexName, type, mappings)).andReturn(true);
        elasticsearchClientWrapper.refreshIndex(newIndexName);
        // Replay
        replayAll();
        // Run test scenario
        indexingComponent.createIndexAndSetupMappings(originalIndex, types);
        // Verify
        verifyAll();
    }
    //endregion

    //region createAliasAndDeleteOldIndices
    @Test
    public void testCreateAliasAndDeleteOldIndicesWithInvalidArguments() {
        // Test data
        // Reset
        resetAll();
        // Expectations
        // Replay
        replayAll();
        // Run test scenario
        try {
            indexingComponent.createAliasAndDeleteOldIndices(null, UUID.randomUUID().toString());
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            indexingComponent.createAliasAndDeleteOldIndices(UUID.randomUUID().toString(), null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAliasAndDeleteOldIndices() {
        // Test data
        final String newIndex = "coolmonkey_" + UUID.randomUUID().toString();
        final String originalIndex = "coolmonkey";
        final String oldIndex1 = "coolmonkey_" + UUID.randomUUID().toString();
        final String oldIndex2 = "coolmonkey_" + UUID.randomUUID().toString();
        final Set<String> clusterIndices = Sets.newHashSet(
                oldIndex1,
                oldIndex2,
                originalIndex,
                newIndex,
                "ankapIndex1"
        );
        // Reset
        resetAll();
        // Expectations
        expect(elasticsearchClientWrapper.addAlias(newIndex, originalIndex)).andReturn(true);
        expect(elasticsearchClientWrapper.getClusterIndices()).andReturn(clusterIndices);
        expect(elasticsearchClientWrapper.deleteIndex(oldIndex1)).andReturn(true);
        expect(elasticsearchClientWrapper.deleteIndex(oldIndex2)).andReturn(true);
        // Replay
        replayAll();
        // Run test scenario
        indexingComponent.createAliasAndDeleteOldIndices(originalIndex, newIndex);
        // Verify
        verifyAll();
    }
    //endregion

    //region removeIndexByName
    @Test
    public void testRemoveIndexByNameWithInvalidArguments() {
        resetAll();
        // test data
        // expectations
        replayAll();
        try {
            indexingComponent.removeIndexByName(null);
            fail();
        } catch (final IllegalArgumentException ignore) {
        }
        verifyAll();
    }

    @Test
    public void testRemoveIndexByName() {
        resetAll();
        // test data
        final String indexName = "new_index";
        final Set<String> indices = new HashSet<>();
        indices.add("new_index_1");
        indices.add("new_index_2");
        indices.add("new_index");
        // expectations
        expect(elasticsearchClientWrapper.getClusterIndices()).andReturn(indices);
        expect(elasticsearchClientWrapper.deleteIndex("new_index")).andReturn(true);
        replayAll();
        indexingComponent.removeIndexByName(indexName);
        verifyAll();
    }
    //endregion

    //endregion

}